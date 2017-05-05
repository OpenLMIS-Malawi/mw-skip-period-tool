package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Facility;

@Repository
public class FacilityRepository extends BaseRepository<Facility> {

  @Override
  String getTableName() {
    return properties.getTableNames().getFacility();
  }

  @Override
  Facility mapRow(Row row) {
    return new Facility(row);
  }
}
