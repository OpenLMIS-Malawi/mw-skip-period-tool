package org.openlmis.migration.tool.scm.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "tblFacilityTypeProducts")
public class FacilityTypeProduct implements Serializable {
  private static final long serialVersionUID = 5275900602653013323L;

  @EmbeddedId
  private ComplexId id;

  @Column(name = "fp_intDEOrder")
  private Short dataEntryOrder;

  @Embeddable
  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class ComplexId implements Serializable {
    private static final long serialVersionUID = 3807864338925087405L;

    @Column(name = "fp_strFacType")
    private String facilityType;

    @Column(name = "fp_lngCatProdID")
    private Integer categoryProductId;

    @Column(name = "fp_lngProdID")
    private Integer productId;

  }

}
