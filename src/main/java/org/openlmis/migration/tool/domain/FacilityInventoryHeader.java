package org.openlmis.migration.tool.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "tblFacilityInventory_Header")
public class FacilityInventoryHeader implements Serializable {
  private static final long serialVersionUID = 7776494224280488438L;

  @Id
  @Column(name = "inv_lngHeaderID")
  private Integer id;

  @Column(name = "inv_strFacCode")
  private String facilityCode;

  @Column(name = "inv_dtmPeriod")
  private LocalDateTime period;

  @Column(name = "inv_fInvOnly")
  private Boolean inventoryOnly;

  @Column(name = "inv_lngCompletedBy")
  private Integer completedBy;

  @Column(name = "inv_memHeaderNote")
  private String notes;

}
