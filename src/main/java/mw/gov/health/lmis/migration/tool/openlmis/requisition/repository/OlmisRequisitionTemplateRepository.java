package mw.gov.health.lmis.migration.tool.openlmis.requisition.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;

import java.util.UUID;

public interface OlmisRequisitionTemplateRepository
    extends CrudRepository<RequisitionTemplate, UUID> {

  RequisitionTemplate findFirstByProgramIdOrderByCreatedDateDesc(UUID programId);

}
