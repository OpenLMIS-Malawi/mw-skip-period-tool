package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

public class Facility extends BaseEntity {

  public Facility(Row row) {
    super(row);
  }

  public String getCode() {
    return getString("Fac_Code");
  }

  public String getName() {
    return getString("Fac_Name");
  }

  public String getType() {
    return getString("Fac_Type");
  }

  public String getSupplyingCode() {
    return getString("Sup_Code");
  }

  public String getProgram() {
    return getString("Program");
  }

  public String getContact() {
    return getString("Fac_Contact");
  }

  public String getAddress1() {
    return getString("Fac_Address1");
  }

  public String getAddress2() {
    return getString("Fac_Address2");
  }

  public String getCity() {
    return getString("Fac_City");
  }

  public String getState() {
    return getString("Fac_State");
  }

  public String getRescode() {
    return getString("Fac_Rescode");
  }

  public String getPhone() {
    return getString("Fac_Phone");
  }

  public String getFax() {
    return getString("Fac_FAX");
  }

  public Boolean getDispense() {
    return getBoolean("Field_Dispense");
  }

  public Double getMaxMonthsOfSupply() {
    return getDouble("MaxMOS");
  }

  public Double getMinMonthsOfSupply() {
    return getDouble("MinMOS");
  }

  public Boolean getActive() {
    return getBoolean("Active");
  }

  public Boolean getWarehouse() {
    return getBoolean("Warehouse");
  }

  public Boolean getHighestLevel() {
    return getBoolean("Highest_LV");
  }

  public Integer getRole() {
    return getInt("Distribution_Role");
  }

  public String getDataCenter() {
    return getString("dc_GuidID");
  }

  public String getId() {
    return getString("fac_guidId");
  }
}
