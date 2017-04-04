package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;

import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.text.SimpleDateFormat;

public class SupplyManagerExtractListener implements ItemReadListener<Main> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SupplyManagerExtractListener.class);

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/YYYY");

  @Override
  public void beforeRead() {
    LOGGER.debug("Read Product Tracking form");
  }

  @Override
  public void afterRead(Main item) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          "Read Product Tracking form ({};{})",
          item.getFacility(), dateFormat.format(item.getProcessingDate())
      );
    }
  }

  @Override
  public void onReadError(Exception exp) {
    LOGGER.error("Cannot read Product Tracking form", exp);
  }
  
}
