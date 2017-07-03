package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.BaseRequisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DuplicateReader implements ItemReader<List<BaseRequisition>> {
  private static LinkedList<List<BaseRequisition>> duplicates = null;

  @Autowired
  private RequisitionRepository requisitionRepository;

  @Override
  public List<BaseRequisition> read() {
    synchronized (DuplicateReader.class) {
      if (null == duplicates) {
        duplicates = requisitionRepository
            .findDuplicates()
            .parallelStream()
            .map(row -> {
              UUID facilityId = UUID.fromString(row[0].toString());
              UUID programId = UUID.fromString(row[1].toString());
              UUID processingPeriodId = UUID.fromString(row[2].toString());

              return requisitionRepository
                  .findByFacilityIdAndProgramIdAndProcessingPeriodId(
                      facilityId, programId, processingPeriodId
                  );
            })
            .filter(list -> null != list && !list.isEmpty())
            .collect(Collectors.toCollection(LinkedList::new));
      }

      return duplicates.pollFirst();
    }
  }
}
