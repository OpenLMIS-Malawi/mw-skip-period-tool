package mw.gov.health.lmis.migration.tool.openlmis.requisition.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;

import java.util.List;
import java.util.UUID;

public interface RequisitionRepository extends JpaRepository<Requisition, UUID> {

  List<Requisition> findByFacilityIdAndProgramIdAndProcessingPeriodId(UUID facilityId,
                                                                      UUID programId,
                                                                      UUID processingPeriodId);

  boolean existsByFacilityIdAndProgramIdAndProcessingPeriodId(UUID facilityId,
                                                              UUID programId,
                                                              UUID processingPeriodId);

  @Query(value = "SELECT r.facilityId, r.programId, r.processingPeriodId"
      + " FROM Requisition r"
      + " GROUP BY r.facilityId, r.programId, r.processingPeriodId"
      + " HAVING count(*) > 1")
  List<Object[]> findDuplicates();

}
