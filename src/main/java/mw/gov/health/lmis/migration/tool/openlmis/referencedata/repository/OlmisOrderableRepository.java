package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisOrderableRepository extends CrudRepository<Orderable, UUID> {

  Orderable findFirstByName(String name);

}
