package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingSchedule;

import java.util.UUID;

public interface OlmisProcessingScheduleRepository
    extends CrudRepository<ProcessingSchedule, UUID> {
}
