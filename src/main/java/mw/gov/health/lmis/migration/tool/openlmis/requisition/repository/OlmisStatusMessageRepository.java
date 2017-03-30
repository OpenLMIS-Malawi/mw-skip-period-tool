package mw.gov.health.lmis.migration.tool.openlmis.requisition.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusMessage;

import java.util.List;
import java.util.UUID;

public interface OlmisStatusMessageRepository extends CrudRepository<StatusMessage, UUID> {

  List<StatusMessage> findByRequisition(Requisition requisition);
  
}
