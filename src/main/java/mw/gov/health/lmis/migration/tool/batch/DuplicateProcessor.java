package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.OpenLmisNumberUtils.isNotZero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DuplicateProcessor implements ItemProcessor<List<Requisition>, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateProcessor.class);

  @Override
  public List<Requisition> process(List<Requisition> item) throws Exception {
    return item
        .stream()
        .filter(this::isEmpty)
        .collect(Collectors.toList());
  }

  private boolean isEmpty(Requisition requisition) {
    List<RequisitionLineItem> lines = requisition.getRequisitionLineItems();
    int lineCount = lines.size();
    int emptyLineCount = (int) lines.stream().filter(this::isEmpty).count();
    boolean empty = lineCount == emptyLineCount;

    if (empty) {
      LOGGER.info("Found empty requisition (all line items have zero values for all columns)");
    } else {
      LOGGER.info("Requisition contains non-zero data. Skipping...");
    }

    return empty;
  }

  private boolean isEmpty(RequisitionLineItem line) {
    if (isNotZero(line.getBeginningBalance())) {
      logContainsNonZeroValue("beginningBalance");
      return false;
    }

    if (isNotZero(line.getTotalReceivedQuantity())) {
      logContainsNonZeroValue("totalReceivedQuantity");
      return false;
    }

    if (isNotZero(line.getTotalLossesAndAdjustments())) {
      logContainsNonZeroValue("totalLossesAndAdjustments");
      return false;
    }

    if (isNotZero(line.getStockOnHand())) {
      logContainsNonZeroValue("stockOnHand");
      return false;
    }

    if (isNotZero(line.getRequestedQuantity())) {
      logContainsNonZeroValue("requestedQuantity");
      return false;
    }

    if (isNotZero(line.getTotalConsumedQuantity())) {
      logContainsNonZeroValue("totalConsumedQuantity");
      return false;
    }

    if (isNotZero(line.getTotal())) {
      logContainsNonZeroValue("total");
      return false;
    }

    if (isNotZero(line.getApprovedQuantity())) {
      logContainsNonZeroValue("approvedQuantity");
      return false;
    }

    if (isNotZero(line.getTotalStockoutDays())) {
      logContainsNonZeroValue("totalStockoutDays");
      return false;
    }

    if (isNotZero(line.getPacksToShip())) {
      logContainsNonZeroValue("packsToShip");
      return false;
    }

    if (isNotZero(line.getNumberOfNewPatientsAdded())) {
      logContainsNonZeroValue("numberOfNewPatientsAdded");
      return false;
    }

    if (isNotZero(line.getAdjustedConsumption())) {
      logContainsNonZeroValue("adjustedConsumption");
      return false;
    }

    if (isNotZero(line.getAverageConsumption())) {
      logContainsNonZeroValue("averageConsumption");
      return false;
    }

    if (isNotZero(line.getMaximumStockQuantity())) {
      logContainsNonZeroValue("maximumStockQuantity");
      return false;
    }

    if (isNotZero(line.getCalculatedOrderQuantity())) {
      logContainsNonZeroValue("calculatedOrderQuantity");
      return false;
    }

    return true;
  }

  private void logContainsNonZeroValue(String field) {
    LOGGER.info("The '{}' field contains a non-zero value", field);
  }

}
