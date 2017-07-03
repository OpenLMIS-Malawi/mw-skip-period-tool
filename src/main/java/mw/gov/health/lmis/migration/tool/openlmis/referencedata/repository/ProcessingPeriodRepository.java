package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProcessingPeriodRepository extends CrudRepository<ProcessingPeriod, UUID> {

  @Query("SELECT p FROM ProcessingPeriod p WHERE p.startDate >= :start AND p.endDate <= :end")
  List<ProcessingPeriod> findInPeriod(@Param("start") LocalDate start,
                                      @Param("end") LocalDate end);

}
