package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisProgramOrderableRepository
    extends CrudRepository<ProgramOrderable, UUID> {
}
