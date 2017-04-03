package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

public class Product extends BaseEntity {

  public Product(Row row) {
    super(row);
  }

  public String getProductId() {
    return getString("strProductID");
  }

  public String getName() {
    return getString("Prod_Name");
  }

  public String getDose() {
    return getString("Prod_Dose");
  }

  public Integer getMethodAssociated() {
    return getInt("mtd_lngMethodID");
  }

  public Double getCoupleYearsOfProtection() {
    return getDouble("Prod_CYP");
  }

  public Boolean getActive() {
    return getBoolean("Prod_Is_Active");
  }

  public Integer getShipmentQuantityIn() {
    return getInt("Prod_Ship_Qty_In");
  }

  public Integer getShipmentQuantityWarehouse() {
    return getInt("Prod_Ship_Qty_Whse");
  }

  public Integer getShipmentQuantityServiceDeliveryPoint() {
    return getInt("Prod_Ship_Qty_SDP");
  }

  public Float getShipmentVolumeInbound() {
    return getFloat("pr_sinVolumeIn");
  }

  public Float getShipmentVolumeWarehouse() {
    return getFloat("pr_sinVolumeWhse");
  }

  public Float getShipmentVolumeServiceDeliveryPoint() {
    return getFloat("pr_sinVolumeSDP");
  }

  public Integer getId() {
    return getInt("Pr_lngProductID");
  }

  public Float getCaseLength() {
    return getFloat("pr_sinCaseLength");
  }

  public Float getCaseWidth() {
    return getFloat("pr_sinCaseWidth");
  }

  public Float getCaseHeight() {
    return getFloat("pr_sinCaseHeight");
  }

  public Boolean getIndicator() {
    return getBoolean("pr_fIndicator");
  }

  public Integer getProductsInCase() {
    return getInt("pr_lngCaseSize");
  }

  public String getProgramName() {
    return getString("Program_Name");
  }

  public Short getDataEntryOrder() {
    return getShort("DE_Order");
  }

  public Float getNumberOfUnitsPerDay() {
    return getFloat("pr_intUnitsPerDay");
  }

  public Boolean getNonFullSupply() {
    return getBoolean("pr_fNonFullSupply");
  }

  public String getFormulation() {
    return getString("Prod_Form");
  }

  public Double getPackSize() {
    return getDouble("Prod_LowestUnitQty");
  }

  public String getMeasurementUnit() {
    return getString("Prod_LowestUnit");
  }

  public String getDescription() {
    return getString("Prod_Description");
  }

  public Boolean getPurposePopup() {
    return getBoolean("fPOU");
  }
}
