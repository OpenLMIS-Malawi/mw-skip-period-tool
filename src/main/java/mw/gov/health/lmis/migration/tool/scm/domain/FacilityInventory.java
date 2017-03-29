package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "tblFacilityInventory")
public class FacilityInventory implements Serializable {
  private static final long serialVersionUID = -6740366980548963679L;

  @Id
  @Column(name = "inv_lngInventoryID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "inv_strFacCode")
  private Facility facility;

  @Column(name = "inv_dtmPeriod")
  private LocalDateTime period;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "inv_lngProductID")
  private Product product;

  @Column(name = "inv_lngCatProdID")
  private Integer categoryProductId;

  @Column(name = "inv_lngOpeningBal")
  private Integer openingBalance;

  @Column(name = "inv_lngReceipts")
  private Integer stockReceived;

  @Column(name = "inv_lngIssues")
  private Integer issues;

  @Column(name = "inv_lngAdjustments")
  private Integer adjustments;

  @Column(name = "inv_sngStock")
  private Integer stockLevel;

  @Column(name = "inv_fInvOnly")
  private Boolean inventoryCount;

  @Column(name = "inv_memNote")
  private String comments;

  @Column(name = "inv_fErrOpenBal")
  private Boolean errorOpeningBalance;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "inv_lngHeaderID")
  private FacilityInventoryHeader header;

}
