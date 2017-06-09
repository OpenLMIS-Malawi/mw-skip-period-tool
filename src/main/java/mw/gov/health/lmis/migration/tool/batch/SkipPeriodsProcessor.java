package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.INITIATED;
import static mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus.SKIPPED;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.FacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusChange;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionTemplateRepository;

import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

@Component
public class SkipPeriodsProcessor extends AppBatchContext
    implements ItemProcessor<String, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SkipPeriodsProcessor.class);

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private RequisitionTemplateRepository requisitionTemplateRepository;

  @Autowired
  private RequisitionRepository requisitionRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public List<Requisition> process(String item) throws Exception {
    List<Requisition> requisitions = Lists.newArrayList();

    String code = MappingHelper.getFacilityCode(toolProperties, item);
    Facility facility = facilityRepository.findByCode(code);

    if (null == facility) {
      LOGGER.error("Can't find facility with code {}", code);
      return requisitions;
    }

    getPeriods()
        .parallelStream()
        .forEach(period -> getPrograms()
            .parallelStream()
            .forEach(program -> execute(requisitions, facility, period, program)));

    return requisitions;
  }

  private void execute(List<Requisition> requisitions, Facility facility, ProcessingPeriod period,
                       Program program) {
    boolean database = requisitionRepository
        .existsByFacilityIdAndProgramIdAndProcessingPeriodId(
            facility.getId(), program.getId(), period.getId()
        );

    if (database) {
      LOGGER.debug(
          "Requisition for facility {}, program {} and period {} exists. Skipping...",
          facility.getCode(), program.getCode(), period.getName()
      );
      return;
    }

    if (!program.getPeriodsSkippable()) {
      LOGGER.error("Program {} does not allow to skipping periods", program.getCode());
      return;
    }

    requisitions.add(create(facility, period, program));
  }

  private Requisition create(Facility facility, ProcessingPeriod period, Program program) {
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

    String timeZoneName = toolProperties.getParameters().getTimeZone();
    TimeZone timeZone = TimeZone.getTimeZone(timeZoneName);
    ZoneId zoneId = timeZone.toZoneId();

    requisition.setCreatedDate(period.getStartDate().atStartOfDay(zoneId));
    requisition.setModifiedDate(requisition.getCreatedDate());
    requisition.setStatus(SKIPPED);
    requisition.setRequisitionLineItems(Lists.newArrayList());

    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, getUser().getId(), INITIATED));
    requisition.getStatusChanges()
        .add(StatusChange.newStatusChange(requisition, getUser().getId(), SKIPPED));

    return requisition;
  }

}
