package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.APPROVED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.AUTHORIZED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.INITIATED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.SUBMITTED;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroup;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroupProgramSchedule;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.FacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.RequisitionGroupProgramScheduleRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusChange;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.service.RequisitionService;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.service.ItemService;
import mw.gov.health.lmis.migration.tool.scm.service.MainService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
public class MigrationProcessor extends BaseItemProcessor<Main, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MigrationProcessor.class);

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private RequisitionTemplateRepository requisitionTemplateRepository;

  @Autowired
  private RequisitionGroupProgramScheduleRepository
      requisitionGroupProgramScheduleRepository;

  @Autowired
  private ItemConverter itemConverter;

  @Autowired
  private ItemService itemService;

  @Autowired
  private MainService mainService;

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private RequisitionRepository requisitionRepository;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Converts the given {@link Main} object into {@link Requisition} object.
   */
  @Override
  public List<Requisition> process(Main item) {
    String code = MappingHelper.getFacilityCode(toolProperties, item.getFacility());
    Facility facility = facilityRepository.findByCode(code);

    if (null == facility) {
      LOGGER.error("Can't find facility with code {}", code);
      return Lists.newArrayList();
    }

    LocalDate processingDate = mainService.getProcessingDate(item);

    if (null == processingDate) {
      LOGGER.error("Can't convert processing date to LocalDate instance");
      return Lists.newArrayList();
    }

    ProcessingPeriod period = getPeriods()
        .stream()
        .filter(elem -> !elem.getStartDate().isBefore(processingDate)
            && !elem.getEndDate().isAfter(processingDate))
        .findFirst()
        .orElse(null);

    if (null == period) {
      LOGGER.error("Can't find period for processing date {}", processingDate);
      return Lists.newArrayList();
    }

    List<Item> items = itemService.search(item.getProcessingDate(), item.getFacility());

    return itemService
        .groupByCategory(items)
        .entrySet()
        .parallelStream()
        .map(entry -> create(entry.getKey(), entry.getValue(), item, facility, period))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private Requisition create(String programCode, Collection<Item> items, Main main,
                             Facility facility, ProcessingPeriod period) {
    Program program = getPrograms()
        .stream()
        .filter(elem -> programCode.equals(elem.getCode().toString()))
        .findFirst()
        .orElse(null);

    if (null == program) {
      LOGGER.error("Can't find program with code {}", programCode);
      return null;
    }

    boolean database = requisitionRepository
        .existsByFacilityIdAndProgramIdAndProcessingPeriodId(
            facility.getId(), program.getId(), period.getId()
        );

    if (database) {
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

    RequisitionTemplate template = requisitionTemplateRepository
        .findFirstByProgramIdOrderByCreatedDateDesc(program.getId());

    requisition.setTemplate(template);
    requisition.setPreviousRequisitions(Lists.newArrayList());
    requisition.setAvailableNonFullSupplyProducts(Sets.newHashSet());
    requisition.setCreatedDate(convert(main.getModifiedDate(), period.getStartDate()));
    requisition.setModifiedDate(convert(main.getModifiedDate(), period.getEndDate()));
    requisition.setStatus(APPROVED);
    requisition.setRequisitionLineItems(itemConverter.convert(items, requisition));

    List<RequisitionGroupProgramSchedule> schedule = requisitionGroupProgramScheduleRepository
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

    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, getUser().getId(), INITIATED));
    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, getUser().getId(), SUBMITTED));
    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, getUser().getId(), AUTHORIZED));
    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, getUser().getId(), APPROVED));

    requisitionService.addStatusMessage(requisition, getUser(), main.getNotes());

    requisitionService.convertToOrder(requisition, getUser(), program, facility);

    return requisition;
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