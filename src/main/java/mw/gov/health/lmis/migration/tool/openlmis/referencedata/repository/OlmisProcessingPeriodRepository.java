package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingSchedule;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OlmisProcessingPeriodRepository extends CrudRepository<ProcessingPeriod, UUID> {

  ProcessingPeriod findByStartDate(LocalDate startDate);

  List<ProcessingPeriod> findByProcessingScheduleAndStartDate(ProcessingSchedule processingSchedule,
                                                              LocalDate startDate);

}
