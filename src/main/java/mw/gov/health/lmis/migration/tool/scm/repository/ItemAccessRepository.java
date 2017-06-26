package mw.gov.health.lmis.migration.tool.scm.repository;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Item;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Repository
public class ItemAccessRepository extends BaseAccessRepository<Item> {
  private Table<String, Date, FormDetails> table = HashBasedTable.create();

  /**
   * Prepare data that would speed up search process.
   */
  public void init() {
    Database database = getDatabase();

    try {
      Cursor cursor = getCursor(database);
      Row row;

      int rows = 0;
      while ((row = cursor.getNextRow()) != null) {
        Item element = mapRow(row);

        FormDetails details = table.get(element.getFacility(), element.getProcessingDate());

        if (null == details) {
          details = new FormDetails();
          details.first = rows;
          details.count = 1;

          table.put(element.getFacility(), element.getProcessingDate(), details);
        } else {
          details.count += 1;
        }

        ++rows;
      }
    } catch (IOException exp) {
      throw new IllegalStateException("Can't retrieve data for table: " + getTableName(), exp);
    } finally {
      IOUtils.closeQuietly(database);
    }
  }

  /**
   * Find items with the given processing date and facility.
   */
  public List<Item> search(java.util.Date processingDate, String facility) {
    FormDetails details = table.get(facility, processingDate);

    if (null == details) {
      return Lists.newArrayList();
    }

    return findAll(
        item ->
            processingDate.equals(item.getProcessingDate()) && facility.equals(item.getFacility()),
        details.first,
        details.count
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

  public static final class FormDetails {
    private int first;
    private int count;
  }

}
