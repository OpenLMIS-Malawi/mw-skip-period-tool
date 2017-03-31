package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.CategoryProductJoin;

@Repository
public class CategoryProductJoinRepository extends BaseRepository<CategoryProductJoin> {

  @Override
  String getTableName() {
    return "tblCategoryProductJoin";
  }

  @Override
  CategoryProductJoin mapRow(Row row) {
    return RowMapper.categoryProductJoin(row);
  }

}
