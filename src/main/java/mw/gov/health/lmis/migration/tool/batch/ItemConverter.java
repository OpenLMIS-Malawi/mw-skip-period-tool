package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.CurrencyConfig.CURRENCY_CODE;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.LineItemFieldsCalculator.calculateTotalLossesAndAdjustments;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.OpenLmisNumberUtils.zeroIfNull;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.PRICE_PER_PACK_IF_NULL;
import static org.apache.commons.lang3.StringUtils.length;

import com.google.common.collect.Lists;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.OnlyId;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.StockAdjustmentReasonRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StockAdjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.AdjustmentType;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentAccessRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentTypeAccessRepository;
import mw.gov.health.lmis.migration.tool.scm.service.ItemService;
import mw.gov.health.lmis.migration.tool.scm.service.ProductService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemConverter extends AppBatchContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(ItemConverter.class);

  @Autowired
  private AdjustmentAccessRepository adjustmentRepository;

  @Autowired
  private AdjustmentTypeAccessRepository adjustmentTypeRepository;

  @Autowired
  private StockAdjustmentReasonRepository stockAdjustmentReasonRepository;

  @Autowired
  private OrderableRepository orderableRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private ItemService itemService;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Converts {@link Item} object into {@link RequisitionLineItem} object.
   */
  public List<RequisitionLineItem> convert(Collection<Item> items, Requisition requisition) {
    List<Integer> ids = items.stream().map(Item::getId).collect(Collectors.toList());
    Map<Integer, List<Adjustment>> adjustments = adjustmentRepository.search(ids);

    return items
        .parallelStream()
        .map(item -> convert(item, requisition, adjustments.get(item.getId())))
        .collect(Collectors.toList());
  }

  private RequisitionLineItem convert(Item item, Requisition requisition,
                                      List<Adjustment> adjustments) {
    String productCode = productService.getProductCode(item.getProduct());
    OnlyId orderable = orderableRepository.findFirstByProductCode(new Code(productCode));

    RequisitionLineItem requisitionLineItem = new RequisitionLineItem();
    requisitionLineItem.setStockAdjustments(Lists.newArrayList());
    requisitionLineItem.setPreviousAdjustedConsumptions(Lists.newArrayList());
    requisitionLineItem.setRequisition(requisition);
    requisitionLineItem.setOrderableId(orderable.getId());
    requisitionLineItem.setPricePerPack(
        Money.of(CurrencyUnit.of(CURRENCY_CODE), PRICE_PER_PACK_IF_NULL)
    );

    requisitionLineItem.setSkipped(false);

    requisitionLineItem.setTotalReceivedQuantity(item.getReceipts());
    requisitionLineItem.setTotalConsumedQuantity(item.getDispensedQuantity());

    List<StockAdjustmentReason> reasons = Lists.newArrayList();

    if (null != adjustments) {
      requisitionLineItem.setStockAdjustments(
          adjustments
              .parallelStream()
              .map(adjustment -> map(adjustment, requisition, reasons))
              .filter(Objects::nonNull)
              .collect(Collectors.toList())
      );
    }

    requisitionLineItem.setTotalLossesAndAdjustments(
        calculateTotalLossesAndAdjustments(requisitionLineItem, reasons)
    );
    requisitionLineItem.setTotalStockoutDays((int) zeroIfNull(item.getStockedOutDays()));
    requisitionLineItem.setStockOnHand(item.getClosingBalance());
    requisitionLineItem.setCalculatedOrderQuantity(item.getCalculatedRequiredQuantity());
    requisitionLineItem.setRequestedQuantity(item.getRequiredQuantity());
    requisitionLineItem.setRequestedQuantityExplanation(
        toolProperties.getParameters().getRequestedQuantityExplanation()
    );
    requisitionLineItem.setAdjustedConsumption(item.getAdjustedDispensedQuantity());
    requisitionLineItem.setNonFullSupply(false);
    requisitionLineItem.setApprovedQuantity(item.getRequiredQuantity());

    requisitionLineItem.setMaxPeriodsOfStock(getMonthsOfStock(requisitionLineItem));

    String remarks = itemService.getNotes(item);

    if (length(remarks) > 250) {
      LOGGER.warn("The remarks ({}) are too long. Skipping...", remarks);
    } else {
      requisitionLineItem.setRemarks(remarks);
    }

    return requisitionLineItem;
  }

  private StockAdjustment map(Adjustment adjustment, Requisition requisition,
                              List<StockAdjustmentReason> reasons) {
    AdjustmentType type = adjustmentTypeRepository.findByType(adjustment.getType());
    String name = MappingHelper.getAdjustmentName(toolProperties, type.getName());

    Program program = getPrograms()
        .stream()
        .filter(elem -> requisition.getProgramId().equals(elem.getId()))
        .findFirst()
        .orElse(null);

    if (null == program) {
      LOGGER.error("Can't find program with id {}", requisition.getProgramId());
      return null;
    }

    StockAdjustmentReason stockAdjustmentReason =
        stockAdjustmentReasonRepository.findByProgramAndName(program, name);

    if (null == stockAdjustmentReason) {
      LOGGER.error(
          "Can't find stock adjustment reason for program {} with name {}",
          program.getCode(), name
      );

      return null;
    }

    StockAdjustment stockAdjustment = new StockAdjustment();
    stockAdjustment.setReasonId(stockAdjustmentReason.getId());
    stockAdjustment.setQuantity(adjustment.getQuantity());

    reasons.add(stockAdjustmentReason);

    return stockAdjustment;
  }

  private BigDecimal getMonthsOfStock(RequisitionLineItem requisitionLineItem) {
    if (0 == zeroIfNull(requisitionLineItem.getAdjustedConsumption())) {
      return BigDecimal.ZERO;
    }

    return BigDecimal.valueOf(requisitionLineItem.getStockOnHand())
        .divide(
            BigDecimal.valueOf(requisitionLineItem.getAdjustedConsumption()),
            1,
            BigDecimal.ROUND_HALF_UP
        );
  }

}
