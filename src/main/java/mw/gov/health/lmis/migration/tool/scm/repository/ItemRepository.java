package mw.gov.health.lmis.migration.tool.scm.repository;


import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ItemRepository extends BaseRepository<Item> {

  /**
   * Find items with the given processing date and facility.
   */
  public List<Item> search(LocalDateTime processingDate, String facility) {
    return search(
        item -> item.getProcessingDate().equals(processingDate)
            && item.getFacility().equals(facility)
    );
  }

  @Override
  String getTableName() {
    return "CTF_Item";
  }

  @Override
  Item mapRow(Row row) {
    return RowMapper.item(row);
  }
}
