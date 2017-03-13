package org.openlmis.migration.tool.domain;

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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "tlkReportingGroupChain")
public class ReportingGroupChain implements Serializable  {
  private static final long serialVersionUID = 1129691435921819904L;

  @EmbeddedId
  private ComplexId id;

  @Embeddable
  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class ComplexId implements Serializable {
    private static final long serialVersionUID = 8189100589320220053L;

    @Column(name = "lngReportingGroupID")
    private Integer reportingGroupId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "strFacCode", referencedColumnName = "Fac_Code")
    private Facility facility;

  }

}
