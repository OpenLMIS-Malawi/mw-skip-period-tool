package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.LineItemFieldsCalculator.calculateTotalLossesAndAdjustments;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StockAdjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.AdjustmentType;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentTypeRepository;

import java.util.List;
import java.util.UUID;

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
  private ToolProperties toolProperties;

  /**
   * Converts {@link Item} object into {@link RequisitionLineItem} object.
   */
  public RequisitionLineItem convert(Item item, UUID programId) {
    RequisitionLineItem requisitionLineItem = new RequisitionLineItem();
    requisitionLineItem.setSkipped(false);

    requisitionLineItem.setTotalReceivedQuantity(item.getReceipts());
    requisitionLineItem.setTotalConsumedQuantity(item.getDispensedQuantity());

    Program program = olmisProgramRepository.findOne(programId);
    List<StockAdjustment> stockAdjustments = Lists.newArrayList();
    for (Adjustment adjustment : adjustmentRepository.search(item.getId())) {
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
    requisitionLineItem.setTotalStockoutDays(item.getStockedOutDays().intValue());
    requisitionLineItem.setStockOnHand(item.getClosingBalance());
    requisitionLineItem.setCalculatedOrderQuantity(item.getCalculatedRequiredQuantity());
    requisitionLineItem.setRequestedQuantity(item.getRequiredQuantity());
    requisitionLineItem.setRequestedQuantityExplanation(
        toolProperties.getParameters().getRequestedQuantityExplanation()
    );
    requisitionLineItem.setAdjustedConsumption(item.getAdjustedDispensedQuantity());
    requisitionLineItem.setNonFullSupply(false);

    return requisitionLineItem;
  }

}
