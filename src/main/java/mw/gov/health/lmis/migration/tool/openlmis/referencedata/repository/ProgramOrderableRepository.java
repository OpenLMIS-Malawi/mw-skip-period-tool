package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProgramOrderableRepository
    extends CrudRepository<ProgramOrderable, UUID> {

  @Query("FROM ProgramOrderable "
      + "WHERE program = :program AND product = :product AND orderableDisplayCategory = :category")
  ProgramOrderable findByProgramAndProductAndCategory(@Param("program") Program program,
                                        @Param("product") Orderable product,
                                        @Param("category") OrderableDisplayCategory category);

}
