package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.openlmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OlmisProgramOrderableRepository
    extends CrudRepository<ProgramOrderable, UUID> {

  @Query("FROM ProgramOrderable "
      + "WHERE program = :program AND product = :product AND orderableDisplayCategory = :category")
  ProgramOrderable findByProgramAndProductAndCategory(@Param("program") Program program,
                                        @Param("product") Orderable product,
                                        @Param("category") OrderableDisplayCategory category);

}
