package org.openlmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "Product")
public class Product implements Serializable {
  private static final long serialVersionUID = 2520947644114935846L;

  @Column(name = "strProductID")
  private String productId;

  @Column(name = "Prod_Name")
  private String name;

  @Column(name = "Prod_Dose")
  private String dose;

  @Column(name = "mtd_lngMethodID")
  private Integer methodAssociated;

  @Column(name = "Prod_CYP")
  private Double coupleYearsOfProtection;

  @Column(name = "Prod_Is_Active")
  private Boolean active;

  @Column(name = "Prod_Ship_Qty_In")
  private Integer shipmentQuantityIn;

  @Column(name = "Prod_Ship_Qty_Whse")
  private Integer shipmentQuantityWarehouse;

  @Column(name = "Prod_Ship_Qty_SDP")
  private Integer shipmentQuantityServiceDeliveryPoint;

  @Column(name = "pr_sinVolumeIn")
  private Double shipmentVolumeInbound;

  @Column(name = "pr_sinVolumeWhse")
  private Double shipmentVolumeWarehouse;

  @Column(name = "pr_sinVolumeSDP")
  private Double shipmentVolumeServiceDeliveryPoint;

  @Id
  @Column(name = "Pr_lngProductID")
  private Integer id;

  @Column(name = "pr_sinCaseLength")
  private Double caseLength;

  @Column(name = "pr_sinCaseWidth")
  private Double caseWidth;

  @Column(name = "pr_sinCaseHeight")
  private Double caseHeight;

  @Column(name = "pr_fIndicator")
  private Boolean indicator;

  @Column(name = "pr_lngCaseSize")
  private Integer productsInCase;

  @Column(name = "Program_Name")
  private String programName;

  @Column(name = "DE_Order")
  private Short dataEntryOrder;

  @Column(name = "pr_intUnitsPerDay")
  private Double numberOfUnitsPerDay;

  @Column(name = "pr_fNonFullSupply")
  private Boolean nonFullSupply;

  @Column(name = "Prod_Form")
  private String formulation;

  @Column(name = "Prod_LowestUnitQty")
  private Double packSize;

  @Column(name = "Prod_LowestUnit")
  private String measurementUnit;

  @Column(name = "Prod_Description")
  private String description;

  @Column(name = "fPOU")
  private Boolean purposePopup;

}
