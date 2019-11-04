package mw.gov.health.lmis.skip.period.tool.openlmis.requisition.repository;

import java.util.UUID;
import mw.gov.health.lmis.skip.period.tool.openlmis.requisition.domain.Requisition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequisitionRepository extends JpaRepository<Requisition, UUID> {

  boolean existsByFacilityIdAndProgramIdAndProcessingPeriodIdAndEmergency(UUID facilityId,
                                                                          UUID programId,
                                                                          UUID processingPeriodId,
                                                                          boolean emergency);

}
