package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class TransformListener
    implements ItemProcessListener<Main, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransformListener.class);

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/YYYY");
  private LocalTime startTime;

  @Override
  public void beforeProcess(Main item) {
    startTime = LocalTime.now();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Convert Product Tracking form ({};{}) to OpenLMIS requisitions",
          item.getFacility(), dateFormat.format(item.getProcessingDate())
      );
    }
  }

  @Override
  public void afterProcess(Main item, List<Requisition> result) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          "Converted Product Tracking form ({};{}) to {} OpenLMIS requisitions in {}s",
          item.getFacility(), dateFormat.format(item.getProcessingDate()), result.size(),
          Duration.between(startTime, LocalTime.now()).getSeconds()
      );
    }
  }

  @Override
  public void onProcessError(Main item, Exception exp) {
    if (LOGGER.isErrorEnabled()) {
      LOGGER.error(
          "Cannot convert Product Tracking form ({};{}) to OpenLMIS requisitions",
          item.getFacility(), dateFormat.format(item.getProcessingDate()), exp
      );
    }
  }

}
