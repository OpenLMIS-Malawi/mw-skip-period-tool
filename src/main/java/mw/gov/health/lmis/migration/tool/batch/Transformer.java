package mw.gov.health.lmis.migration.tool.batch;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.Pair;
import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroupProgramSchedule;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisRequisitionGroupProgramScheduleRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.service.RequisitionService;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.service.ItemService;
import mw.gov.health.lmis.migration.tool.scm.service.ProductService;

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
  private OlmisOrderableRepository olmisOrderableRepository;

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
  private ProductService productService;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Converts the given {@link Main} object into {@link Requisition} object.
   */
  @Override
  public List<Requisition> process(Main main) {
    return itemService
        .groupByCategory(main.getProcessingDate(), main.getFacility())
        .asMap()
        .entrySet()
        .stream()
        .map(entry -> createRequisition(entry.getKey(), entry.getValue(), main))
        .collect(Collectors.toList());
  }

  private Requisition createRequisition(String programCode, Collection<Item> items,
                                        Main main) {
    String code = MappingHelper.getFacilityCode(toolProperties, main.getFacility());
    Facility facility = olmisFacilityRepository.findByCode(code);
    Program program = olmisProgramRepository.findByCode(new Code(programCode));
    ProcessingPeriod period = olmisProcessingPeriodRepository
        .findByStartDate(safeNull(main.getProcessingDate()).toLocalDate());

    Requisition requisition = new Requisition(
        facility.getId(), program.getId(), period.getId(), ExternalStatus.INITIATED, false
    );

    requisition.setNumberOfMonthsInPeriod(period.getDurationInMonths());

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

    User user = olmisUserRepository
        .findByUsername(toolProperties.getParameters().getCreator());

    List<Pair<Orderable, Double>> pairs = items
        .stream()
        .map(item -> {
          String productCode = productService.getProductCode(item.getProduct());
          return new Pair<>(
              olmisOrderableRepository.findFirstByProductCode(new Code(productCode)),
              itemService.getMonthsOfStock(item));
        })
        .collect(Collectors.toList());

    requisition.initiate(template, pairs, previousRequisitions,
        numberOfPreviousPeriodsToAverage, null, user.getId());

    requisition.setAvailableNonFullSupplyProducts(Sets.newHashSet());

    requisition.setCreatedDate(safeNull(main.getCreatedDate()));
    requisition.setModifiedDate(safeNull(main.getModifiedDate()));

    for (RequisitionLineItem line : requisition.getRequisitionLineItems()) {
      updateLine(line, requisition, items);
    }

    List<Orderable> products = Lists.newArrayList(olmisOrderableRepository.findAll());

    requisition.submit(products, user.getId());
    requisition.authorize(products, user.getId());
    requisitionService.addStatusMessage(
        requisition, user, main.getNotes(), itemService.getNotes(items)
    );

    RequisitionGroupProgramSchedule schedule = olmisRequisitionGroupProgramScheduleRepository
        .findByProgramAndFacility(program.getId(), facility.getId());
    requisition.setSupervisoryNodeId(schedule.getRequisitionGroup().getSupervisoryNode().getId());

    requisition.approve(null, products, user.getId());

    requisitionService.convertToOrder(requisition, user, program, facility);

    return requisition;
  }

  private void updateLine(RequisitionLineItem line, Requisition requisition,
                          Collection<Item> items) {
    Orderable orderable = olmisOrderableRepository.findOne(line.getOrderableId());
    Item item = items
        .stream()
        .filter(elem -> productService
            .getProductCode(elem.getProduct())
            .equals(orderable.getProductCode().toString())
        )
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("can't find correct item element from list"));

    line.updateFrom(itemConverter.convert(item, requisition.getProgramId()));
  }

  private ZonedDateTime safeNull(Date date) {
    if (null == date) {
      return null;
    }

    return date
        .toInstant()
        .atZone(TimeZone.getTimeZone(toolProperties.getParameters().getTimeZone()).toZoneId());
  }

}
