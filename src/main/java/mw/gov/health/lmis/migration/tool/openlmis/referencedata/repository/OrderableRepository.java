package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.OnlyId;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;

import java.util.UUID;

public interface OrderableRepository extends CrudRepository<Orderable, UUID> {

  OnlyId findFirstByProductCode(Code productCode);

}
