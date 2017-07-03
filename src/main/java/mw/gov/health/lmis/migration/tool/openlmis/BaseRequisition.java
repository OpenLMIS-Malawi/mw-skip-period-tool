package mw.gov.health.lmis.migration.tool.openlmis;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface BaseRequisition {

  UUID getId();

  List<RequisitionLineItem> getRequisitionLineItems();

  /**
   * Find requisition line item by product id.
   */
  default RequisitionLineItem findLineByProductId(UUID productId) {
    if (null == getRequisitionLineItems()) {
      return null;
    }

    return getRequisitionLineItems()
        .stream()
        .filter(e -> Objects.equals(productId, e.getOrderableId()))
        .findFirst()
        .orElse(null);
  }

}
