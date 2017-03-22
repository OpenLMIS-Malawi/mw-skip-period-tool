package org.openlmis.migration.tool.batch;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

import com.google.common.collect.Lists;

import org.openlmis.migration.tool.openlmis.fulfillment.domain.Order;
import org.openlmis.migration.tool.openlmis.fulfillment.domain.OrderStatus;
import org.openlmis.migration.tool.openlmis.fulfillment.domain.ProofOfDelivery;
import org.openlmis.migration.tool.openlmis.fulfillment.repository.OrderRepository;
import org.openlmis.migration.tool.openlmis.fulfillment.repository.ProofOfDeliveryRepository;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Facility;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.openlmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import org.openlmis.migration.tool.openlmis.requisition.domain.Requisition;
import org.openlmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import org.openlmis.migration.tool.openlmis.requisition.domain.RequisitionStatus;
import org.openlmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import org.openlmis.migration.tool.openlmis.requisition.domain.StockAdjustment;
import org.openlmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;
import org.openlmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import org.openlmis.migration.tool.scm.domain.Adjustment;
import org.openlmis.migration.tool.scm.domain.Item;
import org.openlmis.migration.tool.scm.domain.Main;
import org.openlmis.migration.tool.scm.repository.ItemRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MainProcessor implements ItemProcessor<Main, Requisition> {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private OlmisFacilityRepository olmisFacilityRepository;

  @Autowired
  private OlmisProgramRepository olmisProgramRepository;

  @Autowired
  private OlmisProcessingPeriodRepository olmisProcessingPeriodRepository;

  @Autowired
  private OlmisRequisitionTemplateRepository olmisRequisitionTemplateRepository;

  @Autowired
  private OlmisStockAdjustmentReasonRepository olmisStockAdjustmentReasonRepository;

  @Autowired
  private OlmisOrderableRepository olmisOrderableRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProofOfDeliveryRepository proofOfDeliveryRepository;

  @Autowired
  private OlmisRequisitionRepository olmisRequisitionRepository;

  /**
   * Converts the given {@link Main} object into {@link Requisition} object.
   */
  @Override
  public Requisition process(Main main) {
    List<Item> items = itemRepository.findByProcessingDateAndFacility(
        main.getId().getProcessingDate(), main.getId().getFacility()
    );

    items.sort((o1, o2) -> {
      int compare = o1.getCategoryProduct().getProduct().getProgramName()
          .compareTo(o2.getCategoryProduct().getProduct().getProgramName());

      if (0 != compare) {
        return compare;
      }

      return o1.getCategoryProduct().getOrder().compareTo(o2.getCategoryProduct().getOrder());
    });

    return createRequisition(main, items);
  }

  private Requisition createRequisition(Main main, List<Item> items) {
    ProcessingPeriod period = olmisProcessingPeriodRepository
        .findByStartDate(main.getId().getProcessingDate().toLocalDate().with(firstDayOfMonth()));

    Program program = olmisProgramRepository.findByName(main.getProgramName());
    RequisitionTemplate template = olmisRequisitionTemplateRepository
        .findByProgramId(program.getId());

    Requisition requisition = initRequisition(main, template, program, period);
    requisition.setRequisitionLineItems(
        items
            .stream()
            .map(item -> createLine(item,requisition, template, program, period))
            .collect(Collectors.toList())
    );

    List<Orderable> products = Lists.newArrayList(olmisOrderableRepository.findAll());

    requisition.submit(products, null);
    requisition.authorize(products, null);
    requisition.approve(null, products);

    convertToOrder(requisition);

    return olmisRequisitionRepository.save(requisition);
  }

  private Requisition initRequisition(Main main, RequisitionTemplate template, Program programDto,
                                      ProcessingPeriod processingPeriodDto) {
    org.openlmis.migration.tool.scm.domain.Facility mainFacility = main.getId().getFacility();
    Facility facility = olmisFacilityRepository
        .findByNameAndCode(mainFacility.getName(), mainFacility.getCode());

    Requisition requisition = new Requisition();
    requisition.setFacilityId(facility.getId());
    requisition.setProgramId(programDto.getId());
    requisition.setProcessingPeriodId(processingPeriodDto.getId());
    requisition.setCreatedDate(safeNull(main.getCreatedDate()));
    requisition.setModifiedDate(safeNull(main.getModifiedDate()));
    requisition.setDraftStatusMessage(main.getNotes());
    requisition.setTemplate(template);
    requisition.setNumberOfMonthsInPeriod(processingPeriodDto.getDurationInMonths());
    requisition.setStatus(RequisitionStatus.INITIATED);
    requisition.setEmergency(false);

    return olmisRequisitionRepository.save(requisition);
  }

  private ZonedDateTime safeNull(LocalDateTime dateTime) {
    return null == dateTime
        ? null
        : dateTime.atZone(ZoneId.of("UTC"));
  }

  private RequisitionLineItem createLine(Item item, Requisition requisition,
                                         RequisitionTemplate template, Program program,
                                         ProcessingPeriod processingPeriodDto) {
    Orderable orderableDto = olmisOrderableRepository.findByName(item.getProductName());

    RequisitionLineItem requisitionLineItem = new RequisitionLineItem();
    requisitionLineItem.setMaxPeriodsOfStock(
        BigDecimal.valueOf(processingPeriodDto.getDurationInMonths())
    );
    requisitionLineItem.setRequisition(requisition);
    requisitionLineItem.setSkipped(false);
    requisitionLineItem.setOrderableId(orderableDto.getId());
    requisitionLineItem.setTotalReceivedQuantity(item.getReceipts());
    requisitionLineItem.setTotalConsumedQuantity(item.getDispensedQuantity());

    List<StockAdjustment> stockAdjustments = Lists.newArrayList();
    for (Adjustment adjustment : item.getAdjustments()) {
      StockAdjustmentReason stockAdjustmentReasonDto = olmisStockAdjustmentReasonRepository
          .findByProgramAndName(program, adjustment.getType().getCode());

      StockAdjustment stockAdjustment = new StockAdjustment();
      stockAdjustment.setReasonId(stockAdjustmentReasonDto.getId());
      stockAdjustment.setQuantity(adjustment.getQuantity());

      stockAdjustments.add(stockAdjustment);
    }

    requisitionLineItem.setStockAdjustments(stockAdjustments);
    requisitionLineItem.setTotalStockoutDays(item.getStockedOutDays().intValue());
    requisitionLineItem.setStockOnHand(item.getClosingBalance());
    requisitionLineItem.setCalculatedOrderQuantity(item.getCalculatedRequiredQuantity());
    requisitionLineItem.setRequestedQuantity(item.getRequiredQuantity());
    requisitionLineItem.setRequestedQuantityExplanation("lagacy data");
    requisitionLineItem.setAdjustedConsumption(item.getAdjustedDispensedQuantity());

    requisitionLineItem.setRemarks(item.getId().toString());

    requisitionLineItem.calculateAndSetFields(
        template, Lists.newArrayList(olmisStockAdjustmentReasonRepository.findAll()),
        requisition.getNumberOfMonthsInPeriod()
    );
    return requisitionLineItem;
  }

  private void convertToOrder(Requisition requisition) {
    // TODO: change that (or validate this is correct)
    requisition.setSupplyingFacilityId(requisition.getFacilityId());
    requisition.setStatus(RequisitionStatus.RELEASED);

    Order order = Order.newOrder(requisition);
    order.setStatus(OrderStatus.RECEIVED);
    order.setOrderCode("O" + requisition.getId() + "R");

    // TODO: determine proper values for those properties
    ProofOfDelivery proofOfDelivery = new ProofOfDelivery(order);
    proofOfDelivery.setDeliveredBy(null);
    proofOfDelivery.setReceivedBy(null);
    proofOfDelivery.setReceivedDate(null);

    proofOfDelivery
        .getProofOfDeliveryLineItems()
        .forEach(line -> line.setQuantityReceived(null));

    orderRepository.save(order);
    proofOfDeliveryRepository.save(proofOfDelivery);
  }

}
