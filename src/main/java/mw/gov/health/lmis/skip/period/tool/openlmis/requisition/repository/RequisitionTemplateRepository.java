package mw.gov.health.lmis.skip.period.tool.openlmis.requisition.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.skip.period.tool.openlmis.requisition.domain.RequisitionTemplate;

import java.util.UUID;

public interface RequisitionTemplateRepository
    extends CrudRepository<RequisitionTemplate, UUID> {

  RequisitionTemplate findFirstByProgramIdOrderByCreatedDateDesc(UUID programId);

}
