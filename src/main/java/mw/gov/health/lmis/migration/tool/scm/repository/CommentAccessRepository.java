package mw.gov.health.lmis.migration.tool.scm.repository;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Maps;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Comment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CommentAccessRepository extends PreparedAccessRepository<Comment> {

  @Override
  boolean isNotValid(Comment element) {
    return !isBlank(element.getComment());
  }

  @Override
  Integer mapToKey(Comment element) {
    return element.getItem();
  }

  /**
   * Finds all adjustments for the given item.
   */
  public Map<Integer, List<Comment>> search(List<Integer> itemIds) {
    List<Integer> filtered = getValid(itemIds);

    if (filtered.isEmpty()) {
      return Maps.newHashMap();
    }

    return findAll(
        elem -> isNotBlank(elem.getComment()) && filtered.contains(elem.getItem()),
        new SearchDetails(filtered))
        .stream()
        .collect(Collectors.groupingBy(Comment::getItem));
  }

  @Override
  String getTableName() {
    return properties.getTableNames().getComment();
  }

  @Override
  Comment mapRow(Row row) {
    return new Comment(row);
  }

}
