package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.util.RequisitionUtil;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DuplicateProcessor implements ItemProcessor<List<Requisition>, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateProcessor.class);

  @Override
  public List<Requisition> process(List<Requisition> item) throws Exception {
    return item
        .stream()
        .filter(requisition -> {
          boolean empty = RequisitionUtil.isEmpty(requisition);
          String msg = empty
              ? "Found empty requisition (all line items have zero values for all columns)"
              : "Requisition contains non-zero data. Skipping...";

          LOGGER.info(msg);
          return empty;
        })
        .collect(Collectors.toList());
  }

}
