package mw.gov.health.lmis.migration.tool.openlmis.requisition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;

import java.util.UUID;

public interface RequisitionRepository extends JpaRepository<Requisition, UUID> {

  boolean existsByFacilityIdAndProgramIdAndProcessingPeriodId(UUID facilityId,
                                                              UUID programId,
                                                              UUID processingPeriodId);

}
