package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.APPROVED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.AUTHORIZED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.INITIATED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.SUBMITTED;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroup;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroupProgramSchedule;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisRequisitionGroupProgramScheduleRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusChange;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.service.RequisitionService;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.service.ItemService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
public class Transformer implements ItemProcessor<Main, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Transformer.class);

  @Autowired
  private OlmisFacilityRepository olmisFacilityRepository;

  @Autowired
  private OlmisProgramRepository olmisProgramRepository;

  @Autowired
  private OlmisProcessingPeriodRepository olmisProcessingPeriodRepository;

  @Autowired
  private OlmisRequisitionTemplateRepository olmisRequisitionTemplateRepository;

  @Autowired
  private OlmisUserRepository olmisUserRepository;

  @Autowired
  private OlmisRequisitionGroupProgramScheduleRepository
      olmisRequisitionGroupProgramScheduleRepository;

  @Autowired
  private ItemConverter itemConverter;

  @Autowired
  private ItemService itemService;

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private OlmisRequisitionRepository olmisRequisitionRepository;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Converts the given {@link Main} object into {@link Requisition} object.
   */
  @Override
  public List<Requisition> process(Main item) {
    List<Item> items = itemService.search(item.getProcessingDate(), item.getFacility());

    return itemService
        .groupByCategory(items)
        .entrySet()
        .parallelStream()
        .map(entry -> createRequisition(entry.getKey(), entry.getValue(), item))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private Requisition createRequisition(String programCode, Collection<Item> items, Main main) {
    String code = MappingHelper.getFacilityCode(toolProperties, main.getFacility());
    Facility facility = olmisFacilityRepository.findByCode(code);

    if (null == facility) {
      LOGGER.error("Can't find facility with code {}", code);
      return null;
    }

    Program program = olmisProgramRepository.findByCode(new Code(programCode));

    if (null == program) {
      LOGGER.error("Can't find program with code {}", programCode);
      return null;
    }

    LocalDate processingDate = getProcessingDate(main.getProcessingDate());

    if (null == processingDate) {
      LOGGER.error("Can't convert processing date to LocalDate instance");
      return null;
    }

    ProcessingPeriod period = olmisProcessingPeriodRepository.findInPeriod(processingDate);

    if (null == period) {
      LOGGER.error("Can't find period for processing date {}", processingDate);
      return null;
    }

    List<Requisition> requisitions = olmisRequisitionRepository
        .findByFacilityIdAndProgramIdAndProcessingPeriodId(
            facility.getId(), program.getId(), period.getId()
        );

    if (!isEmpty(requisitions)) {
      LOGGER.warn(
          "Requisition for facility {}, program {} and period {} exists. Skipping...",
          facility.getCode(), program.getCode(), period.getName()
      );
      return null;
    }

    Requisition requisition = new Requisition();
    requisition.setFacilityId(facility.getId());
    requisition.setProgramId(program.getId());
    requisition.setProcessingPeriodId(period.getId());
    requisition.setEmergency(false);
    requisition.setNumberOfMonthsInPeriod(period.getDurationInMonths());

    RequisitionTemplate template = olmisRequisitionTemplateRepository
        .findFirstByProgramIdOrderByCreatedDateDesc(program.getId());

    int numberOfPreviousPeriodsToAverage;
    List<Requisition> previousRequisitions;
    if (template.getNumberOfPeriodsToAverage() == null) {
      numberOfPreviousPeriodsToAverage = 0;
      previousRequisitions = requisitionService.getRecentRequisitions(requisition, 1);
    } else {
      numberOfPreviousPeriodsToAverage = template.getNumberOfPeriodsToAverage() - 1;
      previousRequisitions = requisitionService
          .getRecentRequisitions(requisition, numberOfPreviousPeriodsToAverage);
    }

    if (numberOfPreviousPeriodsToAverage > previousRequisitions.size()) {
      numberOfPreviousPeriodsToAverage = previousRequisitions.size();
    }

    requisition.setTemplate(template);
    requisition.setPreviousRequisitions(previousRequisitions);
    requisition.setAvailableNonFullSupplyProducts(Sets.newHashSet());
    requisition.setCreatedDate(convert(main.getModifiedDate(), period.getStartDate()));
    requisition.setModifiedDate(convert(main.getModifiedDate(), period.getEndDate()));
    requisition.setStatus(APPROVED);
    requisition.setRequisitionLineItems(itemConverter.convert(items, requisition));
    requisition.setPreviousAdjustedConsumptions(numberOfPreviousPeriodsToAverage);

    List<RequisitionGroupProgramSchedule> schedule = olmisRequisitionGroupProgramScheduleRepository
        .findByProgramAndFacility(program.getId(), facility.getId());

    if (!schedule.isEmpty()) {
      RequisitionGroup requisitionGroup = schedule.get(0).getRequisitionGroup();
      requisition.setSupervisoryNodeId(requisitionGroup.getSupervisoryNode().getId());
    } else {
      LOGGER.warn(
          "Can't set supervisory node ID for program {} and facility {}",
          program.getCode(), facility.getCode()
      );
    }

    User user = olmisUserRepository.findByUsername(toolProperties.getParameters().getCreator());

    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, user.getId(), INITIATED));
    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, user.getId(), SUBMITTED));
    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, user.getId(), AUTHORIZED));
    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, user.getId(), APPROVED));

    requisitionService.addStatusMessage(
        requisition, user, main.getNotes(), itemService.getNotes(items)
    );

    requisitionService.convertToOrder(requisition, user, program, facility);

    return requisition;
  }

  private LocalDate getProcessingDate(Date date) {
    String timeZoneName = toolProperties.getParameters().getTimeZone();
    TimeZone timeZone = TimeZone.getTimeZone(timeZoneName);
    ZoneId zoneId = timeZone.toZoneId();

    Instant instant = date.toInstant();
    instant = instant.truncatedTo(ChronoUnit.DAYS);

    return instant.atZone(zoneId).toLocalDate();
  }

  private ZonedDateTime convert(Date date, LocalDate localDate) {
    String timeZoneName = toolProperties.getParameters().getTimeZone();
    TimeZone timeZone = TimeZone.getTimeZone(timeZoneName);
    ZoneId zoneId = timeZone.toZoneId();

    if (null != date) {
      return date.toInstant().atZone(zoneId);
    }

    if (null != localDate) {
      return localDate.atStartOfDay(zoneId);
    }

    return null;
  }

}
