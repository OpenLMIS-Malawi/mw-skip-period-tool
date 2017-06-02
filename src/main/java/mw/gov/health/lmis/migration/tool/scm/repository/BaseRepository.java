package mw.gov.health.lmis.migration.tool.scm.repository;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.ScmDatabaseHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


public abstract class BaseRepository<T> {

  @Autowired
  private ScmDatabaseHandler handler;

  @Autowired
  protected ToolProperties properties;

  /**
   * Find all rows from the given table.
   */
  public List<T> findAll() {
    Database database = handler.getDatabase();

    try {
      Table table = getTable(database);
      List<T> list = Lists.newArrayList();

      for (Row row : table) {
        list.add(mapRow(row));
      }

      return list;
    } finally {
      IOUtils.closeQuietly(database);
    }
  }

  List<T> search(Predicate<T> predicate) {
    Database database = handler.getDatabase();

    try {
      Cursor cursor = getCursor(database);
      List<T> list = Lists.newArrayList();
      Row row;

      while ((row = cursor.getNextRow()) != null) {
        T element = mapRow(row);

        if (predicate.test(element)) {
          list.add(element);
        }
      }

      return list;
    } catch (IOException exp) {
      throw new IllegalStateException("Can't retrieve data for table: " + getTableName(), exp);
    } finally {
      IOUtils.closeQuietly(database);
    }
  }

  /**
   * Find a row by field and value.
   */
  T find(String field, Object value) {
    Database database = handler.getDatabase();

    try {
      Row row = findRow(database, ImmutableMap.of(field, value));
      return null == row ? null : mapRow(row);
    } finally {
      IOUtils.closeQuietly(database);
    }
  }

  abstract String getTableName();

  abstract T mapRow(Row row);

  private Table getTable(Database database) {
    try {
      return database.getTable(getTableName());
    } catch (IOException exp) {
      throw new IllegalStateException("Can't get table: " + getTableName(), exp);
    }
  }

  private Cursor getCursor(Database database) {
    try {
      return CursorBuilder.createCursor(getTable(database));
    } catch (IOException exp) {
      throw new IllegalStateException("Can't create cursors for table: " + getTableName(), exp);
    }
  }

  private Row findRow(Database database, Map<String, Object> rowPattern) {
    try {
      return CursorBuilder.findRow(getTable(database), rowPattern);
    } catch (IOException exp) {
      throw new IllegalStateException("Can't find a row in table: " + getTableName(), exp);
    }
  }

}
