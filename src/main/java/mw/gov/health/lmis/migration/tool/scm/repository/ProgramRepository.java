package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Program;

@Repository
public class ProgramRepository extends BaseRepository<Program> {

  @Override
  String getTableName() {
    return "Program";
  }

  @Override
  Program mapRow(Row row) {
    return RowMapper.program(row);
  }

}
