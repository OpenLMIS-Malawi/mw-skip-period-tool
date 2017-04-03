package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.RowId;

import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class BaseEntity {
  private Row row;

  RowId getRowId() {
    return row.getId();
  }

  String getString(String name) {
    return row.getString(name);
  }

  Boolean getBoolean(String name) {
    return row.getBoolean(name);
  }

  Short getShort(String name) {
    return row.getShort(name);
  }

  Integer getInt(String name) {
    return row.getInt(name);
  }

  Float getFloat(String name) {
    return row.getFloat(name);
  }

  Double getDouble(String name) {
    return row.getDouble(name);
  }

  Date getDate(String name) {
    return row.getDate(name);
  }

}
