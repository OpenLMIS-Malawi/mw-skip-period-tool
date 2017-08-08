package mw.gov.health.lmis.skip.period.tool.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.skip.period.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.skip.period.tool.openlmis.requisition.repository.RequisitionRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequisitionWriter implements ItemWriter<List<Requisition>> {

  @Autowired
  private RequisitionRepository requisitionRepository;

  /**
   * Writes Requisitions into OpenLMIS database.
   */
  @Override
  public synchronized void write(List<? extends List<Requisition>> items) throws Exception {
    requisitionRepository.save(
        items.stream().flatMap(Collection::stream).collect(Collectors.toList())
    );
  }

}
