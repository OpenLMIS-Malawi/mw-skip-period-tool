package mw.gov.health.lmis.migration.tool.scm.repository;

import com.google.common.collect.ImmutableMap;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;

import java.util.List;

@Repository
public class AdjustmentRepository extends BaseRepository<Adjustment> {

  public List<Adjustment> search(Integer itemId) {
    return search(ImmutableMap.of("ctf_ItemID", itemId));
  }

  @Override
  String getTableName() {
    return "CTF_Adjustments";
  }

  @Override
  Adjustment mapRow(Row row) {
    return new Adjustment(row);
  }
}
