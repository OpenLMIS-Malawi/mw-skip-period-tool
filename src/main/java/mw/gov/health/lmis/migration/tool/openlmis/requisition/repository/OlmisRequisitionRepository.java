package mw.gov.health.lmis.migration.tool.openlmis.requisition.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;

import java.util.List;
import java.util.UUID;

public interface OlmisRequisitionRepository extends PagingAndSortingRepository<Requisition, UUID> {

  List<Requisition> findByFacilityIdAndProgramIdAndProcessingPeriodId(UUID facilityId,
                                                                      UUID programId,
                                                                      UUID processingPeriodId);

}
