package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroupProgramSchedule;

import java.util.List;
import java.util.UUID;

public interface RequisitionGroupProgramScheduleRepository
    extends CrudRepository<RequisitionGroupProgramSchedule, UUID> {

  @Query("SELECT rgps FROM RequisitionGroupProgramSchedule rgps "
      + "INNER JOIN rgps.requisitionGroup rg INNER JOIN rg.memberFacilities f "
      + "WHERE f.id = :facility AND rgps.program.id = :program")
  List<RequisitionGroupProgramSchedule> findByProgramAndFacility(@Param("program") UUID program,
                                                                 @Param("facility") UUID facility);

}
