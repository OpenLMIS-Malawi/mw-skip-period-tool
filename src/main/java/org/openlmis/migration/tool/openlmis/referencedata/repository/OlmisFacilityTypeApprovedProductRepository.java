package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.FacilityTypeApprovedProduct;
import org.openlmis.migration.tool.openlmis.referencedata.repository.custom.OlmisFacilityTypeApprovedProductRepositoryCustom;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisFacilityTypeApprovedProductRepository
    extends CrudRepository<FacilityTypeApprovedProduct, UUID>,
    OlmisFacilityTypeApprovedProductRepositoryCustom {
  
}
