package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.service.MainService;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Component
public class TransformListener
    implements ItemProcessListener<Main, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransformListener.class);

  @Autowired
  private MainService mainService;

  private ThreadLocal<LocalTime> startTime = new ThreadLocal<>();

  @Override
  public void beforeProcess(Main item) {
    startTime.set(LocalTime.now());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Convert Product Tracking form ({};{}) to OpenLMIS requisitions",
          item.getFacility(), mainService.getProcessingDate(item)
      );
    }
  }

  @Override
  public void afterProcess(Main item, List<Requisition> result) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          "Converted Product Tracking form ({};{}) to {} OpenLMIS requisitions in {}s",
          item.getFacility(), mainService.getProcessingDate(item), result.size(),
          Duration.between(startTime.get(), LocalTime.now()).getSeconds()
      );
    }
  }

  @Override
  public void onProcessError(Main item, Exception exp) {
    if (LOGGER.isErrorEnabled()) {
      LOGGER.error(
          "Cannot convert Product Tracking form ({};{}) to OpenLMIS requisitions",
          item.getFacility(), mainService.getProcessingDate(item), exp
      );
    }
  }

}
