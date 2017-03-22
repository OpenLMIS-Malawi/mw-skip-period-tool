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
@Entity(name = "tblFacilityCategoryLevels")
public class FacilityCategoryLevel implements Serializable {
  private static final long serialVersionUID = -4632945947171464347L;

  @Id
  @Column(name = "fcl_lngID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "Fac_Code", referencedColumnName = "Fac_Code")
  private Facility facility;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "Program_ID")
  private Program program;

  @Column(name = "Type_Name")
  private String name;

  @Column(name = "fcl_dblMin")
  private Double minimum;

  @Column(name = "fcl_dblMax")
  private Double maximum;

}
