package mw.gov.health.lmis.migration.tool.scm.domain;

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
@Entity(name = "CTF_Main_Users")
public class MainUser implements Serializable {
  private static final long serialVersionUID = 4074723721261730494L;

  @EmbeddedId
  private ComplexId id;

  @Column(name = "ctf_lngTrack1")
  private Integer track1;

  @Column(name = "ctf_lngTrack2")
  private Integer track2;

  @Column(name = "ctf_lngTrack3")
  private Integer track3;

  @Column(name = "ctf_lngTrack4")
  private Integer track4;

  @Embeddable
  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class ComplexId implements Serializable {
    private static final long serialVersionUID = -824955570683271463L;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Fac_Code", referencedColumnName = "Fac_Code")
    private Facility facility;

    @Column(name = "P_Date")
    private LocalDateTime processingDate;

    @Column(name = "bytUse")
    private Short bytUse;

  }

}
