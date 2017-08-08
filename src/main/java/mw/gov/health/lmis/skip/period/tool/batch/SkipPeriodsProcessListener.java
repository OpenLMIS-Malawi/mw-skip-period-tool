package mw.gov.health.lmis.skip.period.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.skip.period.tool.openlmis.requisition.domain.Requisition;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Component
public class SkipPeriodsProcessListener
    implements ItemProcessListener<String, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SkipPeriodsProcessListener.class);

  private ThreadLocal<LocalTime> startTime = new ThreadLocal<>();

  @Override
  public void beforeProcess(String item) {
    startTime.set(LocalTime.now());
    LOGGER.info("Create skipped requisitions for facility: {}", item);
  }

  @Override
  public void afterProcess(String item, List<Requisition> result) {
    LOGGER.info(
        "Created {} skipped requisitions for facility: {} in {}s",
        result.size(), item, Duration.between(startTime.get(), LocalTime.now()).getSeconds()
    );
  }

  @Override
  public void onProcessError(String item, Exception exp) {
    LOGGER.error("Cannot create skipped requisitions for facility: {}", item, exp);
  }

}
