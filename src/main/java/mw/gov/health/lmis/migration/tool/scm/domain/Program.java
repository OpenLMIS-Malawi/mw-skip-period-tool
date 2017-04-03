package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

public class Program extends BaseEntity {

  public Program(Row row) {
    super(row);
  }

  public String getName() {
    return getString("Program_Name");
  }

  public Integer getId() {
    return getInt("Program_ID");
  }

  public Integer getOrder() {
    return getInt("intOrder");
  }

  public Integer getParentId() {
    return getInt("ParentId");
  }
}
