package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class FacilityReaderListener implements ItemReadListener<String> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FacilityReaderListener.class);

  @Override
  public void beforeRead() {
    LOGGER.debug("Read SCM facility");
  }

  @Override
  public void afterRead(String item) {
    LOGGER.info("Read SCM facility: {}", item);
  }

  @Override
  public void onReadError(Exception exp) {
    LOGGER.error("Cannot read SCM facility", exp);
  }

}
