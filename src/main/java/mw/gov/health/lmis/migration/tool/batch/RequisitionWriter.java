package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;

import java.util.List;

@Component
public class RequisitionWriter implements ItemWriter<Requisition> {

  @Autowired
  private OlmisRequisitionRepository olmisRequisitionRepository;

  @Override
  public void write(List<? extends Requisition> items) throws Exception {
    olmisRequisitionRepository.save(items);
  }

}
