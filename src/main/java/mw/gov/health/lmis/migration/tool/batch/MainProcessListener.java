package mw.gov.health.lmis.migration.tool.batch;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

import java.util.List;

public class MainProcessListener implements ItemProcessListener<Main, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainProcessListener.class);

  @Override
  public void beforeProcess(Main item) {
    LOGGER.info(
        "Start converting row from CTF_Main table (facility code {} and processing date {})"
            + "to OpenLMIS requisitions",
        item.getId().getFacility().getCode(), item.getId().getProcessingDate()
    );
  }

  @Override
  public void afterProcess(Main item, List<Requisition> result) {
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
