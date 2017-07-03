package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.BaseRequisition;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Component
public class DuplicateProcessListener
    implements ItemProcessListener<List<BaseRequisition>, List<BaseRequisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateProcessListener.class);

  private ThreadLocal<LocalTime> startTime = new ThreadLocal<>();

  @Override
  public void beforeProcess(List<BaseRequisition> item) {
    startTime.set(LocalTime.now());
    LOGGER.info("Try to set {} duplicates to remove", item.size());
  }

  @Override
  public void afterProcess(List<BaseRequisition> item, List<BaseRequisition> result) {
    LOGGER.info(
        "Set {} duplicates to remove in {}s",
        result.size(), Duration.between(startTime.get(), LocalTime.now()).getSeconds()
    );
  }

  @Override
  public void onProcessError(List<BaseRequisition> item, Exception exp) {
    LOGGER.error("Cannot set {} duplicates to remove", item.size(), exp);
  }

}
