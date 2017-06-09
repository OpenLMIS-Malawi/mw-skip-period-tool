package mw.gov.health.lmis.migration.tool.scm.repository;


import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Item;

import java.util.List;

@Repository
public class ItemAccessRepository extends BaseAccessRepository<Item> {

  /**
   * Find items with the given processing date and facility.
   */
  public List<Item> search(java.util.Date processingDate, String facility) {
    return search(item ->
        processingDate.equals(item.getProcessingDate()) && facility.equals(item.getFacility())
    );
  }

  @Override
  String getTableName() {
    return properties.getTableNames().getItem();
  }

  @Override
  Item mapRow(Row row) {
    return new Item(row);
  }
}
