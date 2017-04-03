package mw.gov.health.lmis.migration.tool.scm.repository;


import com.google.common.collect.ImmutableMap;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Item;

import java.util.List;

@Repository
public class ItemRepository extends BaseRepository<Item> {

  /**
   * Find items with the given processing date and facility.
   */
  public List<Item> search(java.util.Date processingDate, String facility) {
    return search(ImmutableMap.of("P_Date", processingDate, "Fac_Code", facility));
  }

  @Override
  String getTableName() {
    return "CTF_Item";
  }

  @Override
  Item mapRow(Row row) {
    return new Item(row);
  }
}
