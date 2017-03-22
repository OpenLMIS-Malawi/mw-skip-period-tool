package org.openlmis.migration.tool.scm.domain;

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

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "CTF_Demand_Status")
public class DemandStatus implements Serializable {
  private static final long serialVersionUID = -1358681106006555033L;

  @EmbeddedId
  private ComplexId id;

  @Column(name = "LC_Date")
  private LocalDateTime lastChanged;

  @Column(name = "cds_dtmMTLastExport")
  private LocalDateTime lastExported;

  @Embeddable
  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class ComplexId implements Serializable {
    private static final long serialVersionUID = -8919693226237894386L;

    @Column(name = "P_Date")
    private LocalDateTime processingDate;

    @Column(name = "demand_ok")
    private Boolean status;

  }

}
