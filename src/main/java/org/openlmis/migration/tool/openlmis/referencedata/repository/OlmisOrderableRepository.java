package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisOrderableRepository extends CrudRepository<Orderable, UUID> {

  Orderable findByNameIgnoreCase(String name);

}
