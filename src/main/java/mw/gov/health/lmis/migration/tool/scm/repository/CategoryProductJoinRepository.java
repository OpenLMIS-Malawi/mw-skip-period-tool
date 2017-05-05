package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.CategoryProductJoin;

@Repository
public class CategoryProductJoinRepository extends BaseRepository<CategoryProductJoin> {

  @Override
  String getTableName() {
    return properties.getTableNames().getCategoryProductJoin();
  }

  @Override
  CategoryProductJoin mapRow(Row row) {
    return new CategoryProductJoin(row);
  }

  @Cacheable("categoryProductJoins")
  public CategoryProductJoin findById(Integer id) {
    return find("ID", id);
  }

}
