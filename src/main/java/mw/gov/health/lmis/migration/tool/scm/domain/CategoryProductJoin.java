package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

public class CategoryProductJoin extends BaseEntity {

  public CategoryProductJoin(Row row) {
    super(row);
  }

  public Integer getId() {
    return getInt("ID");
  }

  public Integer getProgram() {
    return getInt("lngCategoryID");
  }

  public String getProduct() {
    return getString("strProductID");
  }

  public Integer getOrder() {
    return getInt("intOrder");
  }

  public Integer getDeOrder() {
    return getInt("intDEOrder");
  }
}
