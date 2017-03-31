package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;

import java.io.IOException;
import java.util.List;

@Repository
public class AdjustmentRepository extends BaseRepository<Adjustment> {

  public List<Adjustment> search(Integer itemId) throws IOException {
    return search(adjustment -> adjustment.getItem().equals(itemId));
  }

  @Override
  String getTableName() {
    return "CTF_Adjustments";
  }

  @Override
  Adjustment mapRow(Row row) {
    return RowMapper.adjustment(row);
  }
}
