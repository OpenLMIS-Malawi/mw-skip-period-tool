package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

import mw.gov.health.lmis.migration.tool.Pair;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.Order;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.util.List;

public class TransformListener
    implements ItemProcessListener<Main, List<Pair<Requisition, Order>>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransformListener.class);

  @Override
  public void beforeProcess(Main item) {
    LOGGER.info(
        "Start converting row from CTF_Main table (facility code {} and processing date {})"
            + "to OpenLMIS requisitions",
        item.getId().getFacility().getCode(), item.getId().getProcessingDate()
    );
  }

  @Override
  public void afterProcess(Main item, List<Pair<Requisition, Order>> result) {
    LOGGER.info(
        "Converted row from CTF_Main table (facility code {} and processing date {}) "
            + "to OpenLMIS {} requisitions",
        item.getId().getFacility().getCode(), item.getId().getProcessingDate(), result.size()
    );
  }

  @Override
  public void onProcessError(Main item, Exception exp) {
    LOGGER.error(
        "Cannot convert row from CTF_Main table (facility code {} and processing date {}) "
            + "to OpenLMIS requisitions",
        item.getId().getFacility().getCode(), item.getId().getProcessingDate(), exp
    );
  }

}
