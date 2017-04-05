package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.CurrencyConfig.CURRENCY_CODE;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.LineItemFieldsCalculator.calculateTotalLossesAndAdjustments;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.OpenLmisNumberUtils.zeroIfNull;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.PRICE_PER_PACK_IF_NULL;

import com.google.common.collect.Lists;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StockAdjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.AdjustmentType;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentTypeRepository;
import mw.gov.health.lmis.migration.tool.scm.service.ItemService;
import mw.gov.health.lmis.migration.tool.scm.service.ProductService;

import java.util.List;

@Component
public class ItemConverter {

  @Autowired
  private OlmisProgramRepository olmisProgramRepository;

  @Autowired
  private AdjustmentRepository adjustmentRepository;

  @Autowired
  private AdjustmentTypeRepository adjustmentTypeRepository;

  @Autowired
  private OlmisStockAdjustmentReasonRepository olmisStockAdjustmentReasonRepository;

  @Autowired
  private OlmisOrderableRepository olmisOrderableRepository;

  @Autowired
  private ItemService itemService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Converts {@link Item} object into {@link RequisitionLineItem} object.
   */
  public RequisitionLineItem convert(Item item, Requisition requisition) {
    String productCode = productService.getProductCode(item.getProduct());
    Orderable orderable = olmisOrderableRepository.findFirstByProductCode(new Code(productCode));

    RequisitionLineItem requisitionLineItem = new RequisitionLineItem();
    requisitionLineItem.setStockAdjustments(Lists.newArrayList());
    requisitionLineItem.setPreviousAdjustedConsumptions(Lists.newArrayList());
    requisitionLineItem.setRequisition(requisition);
    requisitionLineItem.setMaxPeriodsOfStock(itemService.getMonthsOfStock(item));
    requisitionLineItem.setOrderableId(orderable.getId());
    requisitionLineItem.setPricePerPack(
        Money.of(CurrencyUnit.of(CURRENCY_CODE), PRICE_PER_PACK_IF_NULL)
    );

    requisitionLineItem.setSkipped(false);

    requisitionLineItem.setTotalReceivedQuantity(item.getReceipts());
    requisitionLineItem.setTotalConsumedQuantity(item.getDispensedQuantity());

    Program program = olmisProgramRepository.findOne(requisition.getProgramId());
    List<StockAdjustment> stockAdjustments = Lists.newArrayList();
    List<Adjustment> adjustments = adjustmentRepository.search(item.getId());

    for (int idx = 0, size = adjustments.size(); idx < size; ++idx) {
      Adjustment adjustment = adjustments.get(idx);

      if (null == adjustment.getQuantity()) {
        continue;
      }

      AdjustmentType type = adjustmentTypeRepository.findByType(adjustment.getType());
      String name = MappingHelper.getAdjustmentName(toolProperties, type.getName());

      StockAdjustmentReason stockAdjustmentReason = olmisStockAdjustmentReasonRepository
          .findByProgramAndName(program, name);

      StockAdjustment stockAdjustment = new StockAdjustment();
      stockAdjustment.setReasonId(stockAdjustmentReason.getId());
      stockAdjustment.setQuantity(adjustment.getQuantity());

      stockAdjustments.add(stockAdjustment);
    }

    requisitionLineItem.setStockAdjustments(stockAdjustments);
    requisitionLineItem.setTotalLossesAndAdjustments(
        calculateTotalLossesAndAdjustments(
            requisitionLineItem,
            Lists.newArrayList(olmisStockAdjustmentReasonRepository.findAll())
        )
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

    return requisitionLineItem;
  }

}
