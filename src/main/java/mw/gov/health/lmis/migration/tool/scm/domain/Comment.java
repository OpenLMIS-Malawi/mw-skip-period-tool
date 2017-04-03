package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

public class Comment extends BaseEntity {

  public Comment(Row row) {
    super(row);
  }

  public Integer getId() {
    return getInt("ctfc_lngID");
  }

  public Integer getItem() {
    return getInt("ctf_ItemID");
  }

  public String getType() {
    return getString("Com_Code");
  }

  public String getComment() {
    return getString("ctfc_txt");
  }
}
