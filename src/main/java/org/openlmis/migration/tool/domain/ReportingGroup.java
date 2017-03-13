package org.openlmis.migration.tool.domain;

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
@Entity(name = "tblReportingGroup")
public class ReportingGroup implements Serializable {
  private static final long serialVersionUID = 6512152703031533268L;

  @Id
  @Column(name = "rg_lngID")
  private Integer id;

  @Column(name = "rg_txtName")
  private String name;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rg_lngParentID")
  private ReportingGroup parent;

  @Column(name = "rg_fAllowMembers")
  private Boolean allowMembers;

  @Column(name = "rg_fAllowRollups")
  private Boolean allowRollups;

  @Column(name = "rg_fIncludeChildMembers")
  private Boolean includeChildMembers;

}
