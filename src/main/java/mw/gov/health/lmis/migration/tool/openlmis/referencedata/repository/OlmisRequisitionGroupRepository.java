package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroup;

import java.util.UUID;

public interface OlmisRequisitionGroupRepository
    extends CrudRepository<RequisitionGroup, UUID> {
}
