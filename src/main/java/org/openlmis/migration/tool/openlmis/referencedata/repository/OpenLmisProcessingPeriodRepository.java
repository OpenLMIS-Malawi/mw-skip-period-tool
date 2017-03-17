package org.openlmis.migration.tool.openlmis.referencedata.repository;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import org.openlmis.migration.tool.domain.SystemDefault;
import org.openlmis.migration.tool.openlmis.InMemoryRepository;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import org.openlmis.migration.tool.repository.SystemDefaultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class OpenLmisProcessingPeriodRepository extends InMemoryRepository<ProcessingPeriod> {

  @Autowired
  private SystemDefaultRepository systemDefaultRepository;

  /**
   * Finds processing period based on start date.
   */
  public ProcessingPeriod findByStartDate(LocalDateTime processingDateTime) {
    LocalDate processingDate = processingDateTime.toLocalDate();
    LocalDate startDate = processingDate.with(firstDayOfMonth());

    ProcessingPeriod found = database
        .values()
        .stream()
        .filter(period -> startDate.equals(period.getStartDate()))
        .findFirst()
        .orElse(null);

    if (null == found) {
      save(create(startDate));
      return findByStartDate(processingDateTime);
    }

    return found;
  }

  private ProcessingPeriod create(LocalDate startDate) {
    SystemDefault systemDefault = systemDefaultRepository
        .findAll()
        .iterator()
        .next();

    long numberOfMonths = systemDefault.getReportingPeriod() - 1L;

    LocalDate endDate = startDate.plusMonths(numberOfMonths).with(lastDayOfMonth());

    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    processingPeriod.setId(UUID.randomUUID());
    processingPeriod.setStartDate(startDate);
    processingPeriod.setEndDate(endDate);

    return processingPeriod;
  }

}
