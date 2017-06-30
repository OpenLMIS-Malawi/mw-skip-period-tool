package mw.gov.health.lmis.migration.tool.scm.repository;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;

import org.apache.commons.io.IOUtils;

import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class PreparedAccessRepository<T> extends BaseAccessRepository<T> {
  private Multimap<Integer, Integer> validItemIds = HashMultimap.create();

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
        T element = mapRow(row);

        if (isNotValid(element)) {
          continue;
        }

        validItemIds.put(mapToKey(element), rows);
        ++rows;
      }
    } catch (IOException exp) {
      throw new IllegalStateException("Can't retrieve data for table: " + getTableName(), exp);
    } finally {
      IOUtils.closeQuietly(database);
    }

    logger.info("Prepared data that will speed up the search process");
  }

  List<Integer> getValid(List<Integer> keys) {
    return keys
        .stream()
        .filter(validItemIds::containsKey)
        .collect(Collectors.toList());
  }

  abstract boolean isNotValid(T element);

  abstract Integer mapToKey(T element);

  @Getter
  final class SearchDetails implements Details {
    private int first;
    private int count;

    SearchDetails(List<Integer> list) {
      List<Integer> rows = validItemIds
          .entries()
          .stream()
          .filter(entry -> list.contains(entry.getKey()))
          .map(Map.Entry::getValue)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());

      count = rows.size();
      first = count == 1
          ? rows.get(0)
          : rows.stream().min(Integer::compare).orElse(0);
    }
  }

}
