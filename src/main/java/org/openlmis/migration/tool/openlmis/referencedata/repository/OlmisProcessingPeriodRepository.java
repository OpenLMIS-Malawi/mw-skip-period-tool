package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface OlmisProcessingPeriodRepository extends CrudRepository<ProcessingPeriod, UUID> {

  ProcessingPeriod findByStartDate(LocalDate startDate);

}
