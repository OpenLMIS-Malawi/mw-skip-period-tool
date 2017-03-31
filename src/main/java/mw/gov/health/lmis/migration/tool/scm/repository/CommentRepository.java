package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Comment;

import java.io.IOException;
import java.util.List;

@Repository
public class CommentRepository extends BaseRepository<Comment> {

  @Override
  String getTableName() {
    return "CTF_Comments";
  }

  @Override
  Comment mapRow(Row row) {
    return RowMapper.comment(row);
  }

  public List<Comment> search(Integer itemId) throws IOException {
    return search(comment -> comment.getItem().equals(itemId));
  }
}
