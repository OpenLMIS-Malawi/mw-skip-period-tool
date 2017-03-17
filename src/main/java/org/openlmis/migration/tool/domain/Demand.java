package org.openlmis.migration.tool.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "CTF_Demand")
public class Demand implements Serializable {
  private static final long serialVersionUID = 3365777552017381867L;

  @EmbeddedId
  private ComplexId id;

  @Column(name = "Prod_Name")
  private String productName;

  @Column(name = "Prod_Dose")
  private String productDose;

  @Column(name = "Demand")
  private Integer averageMonthlyConsumption;

  @Column(name = "New_Visits")
  private Integer newVisits;

  @Column(name = "Cont_Visits")
  private Integer continuingVisits;

  @Column(name = "Dispensed")
  private Integer dispensed;

  @Embeddable
  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class ComplexId implements Serializable {
    private static final long serialVersionUID = -1236493604369621785L;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Fac_Code", referencedColumnName = "Fac_Code")
    private Facility facility;

    @Column(name = "P_Date")
    private LocalDateTime processingDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lngCatProdID")
    private CategoryProductJoin categoryProduct;

  }

}
