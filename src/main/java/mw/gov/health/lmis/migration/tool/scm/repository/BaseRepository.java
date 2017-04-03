package mw.gov.health.lmis.migration.tool.scm.repository;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import org.springframework.beans.factory.annotation.Autowired;

import mw.gov.health.lmis.migration.tool.scm.ScmDatabaseHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public abstract class BaseRepository<T> {

  @Autowired
  private ScmDatabaseHandler handler;

  /**
   * Find all rows from the given table.
   */
  public List<T> findAll() {
    Table table = getTable();

    Iterator<Row> iterator = table.iterator();
    Stream<Row> stream = asStream(iterator);

    return stream
        .map(this::mapRow)
        .collect(Collectors.toList());
  }

  List<T> search(Map<String, Object> rowPattern) {
    Cursor cursor = getCursor();
    List<Row> found = Lists.newArrayList();

    try {
      while (cursor.findNextRow(rowPattern)) {
        found.add(cursor.getCurrentRow());
      }
    } catch (IOException exp) {
      throw new IllegalStateException(
          "There was an issue with retriving a row from table: " + getTableName(), exp
      );
    }

    return found
        .stream()
        .map(this::mapRow)
        .collect(Collectors.toList());
  }

  /**
   * Find a row by field and value.
   */
  T find(String field, Object value) {
    return mapRow(findRow(ImmutableMap.of(field, value)));
  }

  abstract String getTableName();

  abstract T mapRow(Row row);

  private Table getTable() {
    try {
      return handler.getDatabase().getTable(getTableName());
    } catch (IOException exp) {
      throw new IllegalStateException("Can't get table: " + getTableName(), exp);
    }
  }

  Cursor getCursor() {
    try {
      return CursorBuilder.createCursor(getTable());
    } catch (IOException exp) {
      throw new IllegalStateException("Can't create cursors for table: " + getTableName(), exp);
    }
  }

  private Row findRow(Map<String, Object> rowPattern) {
    try {
      return CursorBuilder.findRow(getTable(), rowPattern);
    } catch (IOException exp) {
      throw new IllegalStateException("Can't find a row in table: " + getTableName(), exp);
    }
  }

  private Stream<Row> asStream(Iterator<Row> iterator) {
    Iterable<Row> iterable = () -> iterator;
    return StreamSupport.stream(iterable.spliterator(), false);
  }

}
