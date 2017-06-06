package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.service.MainService;

@Component
public class SupplyManagerExtractListener implements ItemReadListener<Main> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SupplyManagerExtractListener.class);

  @Autowired
  private MainService mainService;

  @Override
  public void beforeRead() {
    LOGGER.debug("Read Product Tracking form");
  }

  @Override
  public void afterRead(Main item) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          "Read Product Tracking form ({};{})",
          item.getFacility(), mainService.getProcessingDate(item)
      );
    }
  }

  @Override
  public void onReadError(Exception exp) {
    LOGGER.error("Cannot read Product Tracking form", exp);
  }
  
}
