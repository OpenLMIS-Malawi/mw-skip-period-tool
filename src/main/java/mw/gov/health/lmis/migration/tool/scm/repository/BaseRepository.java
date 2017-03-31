package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import org.springframework.beans.factory.annotation.Autowired;

import mw.gov.health.lmis.migration.tool.scm.ScmDatabase;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public abstract class BaseRepository<T> {

  @Autowired
  private ScmDatabase database;

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

  List<T> search(Predicate<T> predicate) {
    return findAll()
        .stream()
        .filter(predicate)
        .collect(Collectors.toList());
  }

  /**
   * Find a row by field and value.
   */
  public T find(String field, Object value) {
    Table table = getTable();
    Iterator<Row> iterator = table.iterator();
    Stream<Row> stream = asStream(iterator);
    Row row = stream
        .filter(element -> element.get(field).equals(value))
        .findFirst()
        .orElse(null);

    return mapRow(row);
  }

  abstract String getTableName();

  abstract T mapRow(Row row);

  private Table getTable() {
    try {
      return database.getDatabase().getTable(getTableName());
    } catch (IOException exp) {
      throw new IllegalStateException("Can't get table: " + getTableName(), exp);
    }
  }

  private Stream<Row> asStream(Iterator<Row> iterator) {
    Iterable<Row> iterable = () -> iterator;
    return StreamSupport.stream(iterable.spliterator(), false);
  }

}
