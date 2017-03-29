package mw.gov.health.lmis.migration.tool.openlmis.requisition.repository;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisRequisitionTemplateRepository
    extends CrudRepository<RequisitionTemplate, UUID> {

  RequisitionTemplate findByProgramId(UUID programId);

}
