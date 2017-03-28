package org.openlmis.migration.tool.batch;

import org.openlmis.migration.tool.openlmis.requisition.domain.Requisition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;

import java.util.Collection;
import java.util.List;

public class RequisitionWriteListener implements ItemWriteListener<List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequisitionWriteListener.class);

  @Override
  public void beforeWrite(List<? extends List<Requisition>> items) {
    LOGGER.info(
        "Save {} requisitions to database",
        items.stream().mapToLong(Collection::size).sum()
    );
  }

  @Override
  public void afterWrite(List<? extends List<Requisition>> items) {
    LOGGER.info(
        "Saved {} requisitions to database",
        items.stream().mapToLong(Collection::size).sum()
    );
  }

  @Override
  public void onWriteError(Exception exp, List<? extends List<Requisition>> items) {
    LOGGER.error(
        "Cannot save {} requisitions to database",
        items.stream().mapToLong(Collection::size).sum(),
        exp
    );
  }
  
}
