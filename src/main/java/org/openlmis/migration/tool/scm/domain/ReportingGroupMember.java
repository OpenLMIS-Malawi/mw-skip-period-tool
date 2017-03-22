package org.openlmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

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
@Entity(name = "tblReportingGroupMembers")
public class ReportingGroupMember implements Serializable {
  private static final long serialVersionUID = -3663990371044259908L;

  @Id
  @Column(name = "rgm_lngID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rg_lngID")
  private ReportingGroup reportingGroup;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rgm_FacCode")
  private Facility facility;

}
