package mw.gov.health.lmis.migration.tool.scm.repository;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.ImmutableMap;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Comment;

import java.util.List;

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
  public List<Comment> search(Integer itemId) {
    return search(
        ImmutableMap.of("ctf_ItemID", itemId),
        arg -> isNotBlank(arg.getComment())
    );
  }
}
