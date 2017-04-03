package mw.gov.health.lmis.migration.tool.openlmis.referencedata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.service.ProcessingPeriodService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ProcessingPeriodServiceImpl implements ProcessingPeriodService {

  @Autowired
  private OlmisProcessingPeriodRepository olmisProcessingPeriodRepository;

  @Override
  public List<ProcessingPeriod> findPreviousPeriods(UUID periodId, int amount) {
    ProcessingPeriod period = olmisProcessingPeriodRepository.findOne(periodId);

    if (null == period) {
      return Collections.emptyList();
    }

    Collection<ProcessingPeriod> collection = olmisProcessingPeriodRepository
        .findByProcessingScheduleAndStartDate(
            period.getProcessingSchedule(),
            period.getStartDate()
        );

    if (null == collection || collection.isEmpty()) {
      return Collections.emptyList();
    }

    // create a list...
    List<ProcessingPeriod> list = new ArrayList<>(collection);
    // ...remove the latest period from the list...
    list.removeIf(p -> p.getId().equals(periodId));
    // .. and sort elements by startDate property DESC.
    list.sort(Comparator.comparing(ProcessingPeriod::getStartDate).reversed());

    if (amount > list.size()) {
      return list;
    }

    return list.subList(0, amount);
  }

}
