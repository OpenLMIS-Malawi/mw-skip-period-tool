package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

public class AdjustmentType extends BaseEntity {

  public AdjustmentType(Row row) {
    super(row);
  }

  public String getCode() {
    return getString("Type_Code");
  }

  public String getName() {
    return getString("Type_Name");
  }

  public Boolean getNegative() {
    return getBoolean("Always_Negative");
  }

  public Boolean getUserCanEnter() {
    return getBoolean("UserCanEnter");
  }

  public Boolean getActive() {
    return getBoolean("Adj_Active");
  }

  public String getId() {
    return getString("adj_guidId");
  }
}
