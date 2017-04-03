package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.SystemDefault;

@Repository
public class SystemDefaultRepository extends BaseRepository<SystemDefault> {

  @Override
  String getTableName() {
    return "Sys_Defaults";
  }

  @Override
  SystemDefault mapRow(Row row) {


    return new SystemDefault(row);
  }

}
