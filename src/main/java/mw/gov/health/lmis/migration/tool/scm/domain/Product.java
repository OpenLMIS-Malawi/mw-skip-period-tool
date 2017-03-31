package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Product {
  private String productId;
  private String name;
  private String dose;
  private Integer methodAssociated;
  private Double coupleYearsOfProtection;
  private Boolean active;
  private Integer shipmentQuantityIn;
  private Integer shipmentQuantityWarehouse;
  private Integer shipmentQuantityServiceDeliveryPoint;
  private Float shipmentVolumeInbound;
  private Float shipmentVolumeWarehouse;
  private Float shipmentVolumeServiceDeliveryPoint;
  private Integer id;
  private Float caseLength;
  private Float caseWidth;
  private Float caseHeight;
  private Boolean indicator;
  private Integer productsInCase;
  private String programName;
  private Short dataEntryOrder;
  private Float numberOfUnitsPerDay;
  private Boolean nonFullSupply;
  private String formulation;
  private Double packSize;
  private String measurementUnit;
  private String description;
  private Boolean purposePopup;
}
