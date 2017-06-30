package mw.gov.health.lmis.migration.tool.scm.repository;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Repository;

import lombok.Getter;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Repository
public class ItemAccessRepository extends PreparedAccessRepository<Item> {
  private Table<String, Date, FormDetails> table = HashBasedTable.create();

  /**
   * Prepare data that will speed up the search process.
   */
  @Override
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

    logger.info("Prepared data that will speed up the item search process");
  }

  @Override
  boolean isNotValid(Item element) {
    throw new UnsupportedOperationException();
  }

  @Override
  Integer mapToKey(Item element) {
    throw new UnsupportedOperationException();
  }

  /**
   * Find items with the given processing date and facility.
   */
  public List<Item> search(Date date, String facility) {
    FormDetails details = table.get(facility, date);

    if (null == details) {
      return Lists.newArrayList();
    }

    return findAll(
        item -> date.equals(item.getProcessingDate()) && facility.equals(item.getFacility()),
        details
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

  @Getter
  private static final class FormDetails implements Details {
    private int first;
    private int count;
  }

}
