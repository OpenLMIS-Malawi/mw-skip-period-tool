package mw.gov.health.lmis.migration.tool.scm.repository;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Comment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CommentRepository extends BaseRepository<Comment> {

  @Override
  String getTableName() {
    return "CTF_Comments";
  }

  @Override
  Comment mapRow(Row row) {
    return new Comment(row);
  }

  /**
   * Retrieves all comments for the given item.
   */
  public Map<Integer, List<Comment>> search(List<Integer> itemIds) {
    return search(elem -> isNotBlank(elem.getComment()) && itemIds.contains(elem.getItem()))
        .stream()
        .collect(Collectors.groupingBy(Comment::getItem));
  }
}
