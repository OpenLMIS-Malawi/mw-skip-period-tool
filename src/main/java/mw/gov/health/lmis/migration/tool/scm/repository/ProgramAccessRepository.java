package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Program;

@Repository
public class ProgramAccessRepository extends BaseAccessRepository<Program> {

  @Override
  String getTableName() {
    return properties.getTableNames().getProgram();
  }

  @Override
  Program mapRow(Row row) {
    return new Program(row);
  }

  @Cacheable("programs")
  public Program findByProgramId(Integer productId) {
    return find("Program_ID", productId);
  }

}
