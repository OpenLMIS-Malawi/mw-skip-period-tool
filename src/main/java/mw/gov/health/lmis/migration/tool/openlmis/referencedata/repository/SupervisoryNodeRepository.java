package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.SupervisoryNode;

import java.util.UUID;

public interface SupervisoryNodeRepository
    extends CrudRepository<SupervisoryNode, UUID> {
}
