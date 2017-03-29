package mw.gov.health.lmis.migration.tool.openlmis.requisition.repository;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisStatusMessageRepository extends CrudRepository<StatusMessage, UUID> {
}
