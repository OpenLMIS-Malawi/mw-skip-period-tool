package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;

import java.util.UUID;

public interface OlmisOrderableRepository extends CrudRepository<Orderable, UUID> {

  Orderable findFirstByProductCode(Code productCode);

}
