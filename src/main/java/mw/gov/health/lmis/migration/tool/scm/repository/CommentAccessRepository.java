package mw.gov.health.lmis.migration.tool.scm.repository;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Comment;

import java.util.List;

@Repository
public class CommentAccessRepository extends BaseAccessRepository<Comment> {

  @Override
  String getTableName() {
    return properties.getTableNames().getComment();
  }

  @Override
  Comment mapRow(Row row) {
    return new Comment(row);
  }

  /**
   * Retrieves all comments for the given item.
   */
  public List<Comment> search(Integer itemId) {
    return findAll(elem -> itemId.equals(elem.getItem()) && isNotBlank(elem.getComment()));
  }
}
