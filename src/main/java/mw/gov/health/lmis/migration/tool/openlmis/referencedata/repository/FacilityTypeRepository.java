package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FacilityTypeRepository extends CrudRepository<FacilityType, UUID> {
}
