package mw.gov.health.lmis.skip.period.tool.openlmis.requisition.repository;

import java.util.List;
import java.util.UUID;
import mw.gov.health.lmis.skip.period.tool.openlmis.requisition.domain.RequisitionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequisitionTemplateRepository extends
    JpaRepository<RequisitionTemplate, UUID> {

  @Query("SELECT t FROM RequisitionTemplate AS t WHERE t.archived IS FALSE")
  List<RequisitionTemplate> getActiveTemplates();

  @Query("SELECT DISTINCT t"
      + " FROM RequisitionTemplate AS t"
      + "   INNER JOIN FETCH t.templateAssignments AS a"
      + " WHERE a.programId = :programId"
      + "   AND a.facilityTypeId = :facilityTypeId"
      + "   AND t.archived IS FALSE")
  RequisitionTemplate findTemplate(@Param("programId") UUID program,
      @Param("facilityTypeId") UUID facilityType);

}
