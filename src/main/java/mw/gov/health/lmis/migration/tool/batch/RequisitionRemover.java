package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.BaseRequisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;

import java.util.Collection;
import java.util.List;

@Component
public class RequisitionRemover implements ItemWriter<List<BaseRequisition>> {

  @Autowired
  private RequisitionRepository requisitionRepository;

  @Override
  public synchronized void write(List<? extends List<BaseRequisition>> items) throws Exception {
    items
        .stream()
        .flatMap(Collection::stream)
        .map(BaseRequisition::getId)
        .forEach(requisitionRepository::delete);
  }

}
