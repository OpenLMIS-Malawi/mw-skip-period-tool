package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import org.openlmis.migration.tool.openlmis.referencedata.domain.FacilityTypeApprovedProduct;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import org.openlmis.migration.tool.openlmis.referencedata.repository.custom.OlmisFacilityTypeApprovedProductRepositoryCustom;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisFacilityTypeApprovedProductRepository
    extends CrudRepository<FacilityTypeApprovedProduct, UUID>,
    OlmisFacilityTypeApprovedProductRepositoryCustom {

  FacilityTypeApprovedProduct findByFacilityTypeAndProgramOrderable(FacilityType facilityType,
                                                              ProgramOrderable programOrderable);
}
