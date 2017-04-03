package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

public class Adjustment extends BaseEntity {

  public Adjustment(Row row) {
    super(row);
  }

  public Integer getId() {
    return getInt("cta_lngID");
  }

  public Integer getItem() {
    return getInt("ctf_ItemID");
  }

  public String getType() {
    return getString("Type_Code");
  }

  public Integer getQuantity() {
    return getInt("cta_lngQty");
  }
}
