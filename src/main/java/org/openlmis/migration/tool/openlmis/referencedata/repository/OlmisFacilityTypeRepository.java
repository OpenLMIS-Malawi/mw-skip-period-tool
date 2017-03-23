package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisFacilityTypeRepository extends CrudRepository<FacilityType, UUID> {
}
