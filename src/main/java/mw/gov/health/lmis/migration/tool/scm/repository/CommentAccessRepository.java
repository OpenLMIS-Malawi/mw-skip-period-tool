package mw.gov.health.lmis.migration.tool.scm.repository;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Repository;

import lombok.Getter;
import mw.gov.health.lmis.migration.tool.scm.domain.Comment;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class CommentAccessRepository extends BaseAccessRepository<Comment> {
  private Multimap<Integer, Integer> validItemIds = HashMultimap.create();

  /**
   * Prepare data that would speed up search process.
   */
  @Override
  public void init() {
    Database database = getDatabase();

    try {
      Cursor cursor = getCursor(database);
      Row row;

      int rows = 0;
      while ((row = cursor.getNextRow()) != null) {
        Comment element = mapRow(row);

        if (!isBlank(element.getComment())) {
          continue;
        }

        validItemIds.put(element.getItem(), rows);
        ++rows;
      }
    } catch (IOException exp) {
      throw new IllegalStateException("Can't retrieve data for table: " + getTableName(), exp);
    } finally {
      IOUtils.closeQuietly(database);
    }

    logger.info("Prepared data that will speed up the comment search process");
  }

  @Override
  String getTableName() {
    return properties.getTableNames().getComment();
  }

  @Override
  Comment mapRow(Row row) {
    return new Comment(row);
  }

  /**
   * Finds all adjustments for the given item.
   */
  public Map<Integer, List<Comment>> search(List<Integer> itemIds) {
    List<Integer> filtered = itemIds
        .stream()
        .filter(validItemIds::containsKey)
        .collect(Collectors.toList());

    if (filtered.isEmpty()) {
      return Maps.newHashMap();
    }

    return findAll(
        elem -> isNotBlank(elem.getComment()) && filtered.contains(elem.getItem()),
        new SearchDetails(filtered))
        .stream()
        .collect(Collectors.groupingBy(Comment::getItem));
  }

  @Getter
  private final class SearchDetails implements Details {
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
