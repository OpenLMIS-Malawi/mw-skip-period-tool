package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.APPROVED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.AUTHORIZED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.INITIATED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.SUBMITTED;

import com.google.common.collect.Sets;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
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
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.service.RequisitionService;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.service.ItemService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
public class Transformer implements ItemProcessor<Main, List<Requisition>> {

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
        .collect(Collectors.toList());
  }

  private Requisition createRequisition(String programCode, Collection<Item> items, Main main) {
    String code = MappingHelper.getFacilityCode(toolProperties, main.getFacility());
    Facility facility = olmisFacilityRepository.findByCode(code);
    Program program = olmisProgramRepository.findByCode(new Code(programCode));
    ProcessingPeriod period = olmisProcessingPeriodRepository
        .findByStartDate(convert(main.getProcessingDate()).toLocalDate());

    Requisition requisition = new Requisition();
    requisition.setFacilityId(facility.getId());
    requisition.setProgramId(program.getId());
    requisition.setProcessingPeriodId(period.getId());
    requisition.setEmergency(false);
    requisition.setNumberOfMonthsInPeriod(period.getDurationInMonths());
    requisition.setCreatedDate(convert(period.getStartDate()));
    requisition.setModifiedDate(convert(period.getStartDate()));

    RequisitionTemplate template = olmisRequisitionTemplateRepository
        .findByProgramId(program.getId());

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
    requisition.setCreatedDate(convert(main.getCreatedDate()));
    requisition.setModifiedDate(convert(main.getModifiedDate()));
    requisition.setStatus(APPROVED);
    requisition.setRequisitionLineItems(itemConverter.convert(items, requisition));
    requisition.setPreviousAdjustedConsumptions(numberOfPreviousPeriodsToAverage);

    RequisitionGroupProgramSchedule schedule = olmisRequisitionGroupProgramScheduleRepository
        .findByProgramAndFacility(program.getId(), facility.getId()).get(0);

    requisition.setSupervisoryNodeId(schedule.getRequisitionGroup().getSupervisoryNode().getId());

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

    //requisitionService.convertToOrder(requisition, user, program, facility);

    return requisition;
  }

  private ZonedDateTime convert(LocalDate date) {
    return date
        .atStartOfDay()
        .atZone(TimeZone.getTimeZone(toolProperties.getParameters().getTimeZone()).toZoneId());
  }

  private ZonedDateTime convert(Date date) {
    return date
        .toInstant()
        .atZone(TimeZone.getTimeZone(toolProperties.getParameters().getTimeZone()).toZoneId());
  }

}
