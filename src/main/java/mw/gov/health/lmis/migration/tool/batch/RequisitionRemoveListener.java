package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.BaseRequisition;

import java.util.Collection;
import java.util.List;

@Component
public class RequisitionRemoveListener implements ItemWriteListener<List<BaseRequisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequisitionRemoveListener.class);

  @Override
  public void beforeWrite(List<? extends List<BaseRequisition>> items) {
    LOGGER.info(
        "Remove {} requisitions from database",
        items.stream().mapToLong(Collection::size).sum()
    );
  }

  @Override
  public void afterWrite(List<? extends List<BaseRequisition>> items) {
    LOGGER.info(
        "Removed {} requisitions from database",
        items.stream().mapToLong(Collection::size).sum()
    );
  }

  @Override
  public void onWriteError(Exception exp, List<? extends List<BaseRequisition>> items) {
    LOGGER.error(
        "Cannot remove {} requisitions from database",
        items.stream().mapToLong(Collection::size).sum(),
        exp
    );
  }

}
