package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "tblRoutes")
public class Route implements Serializable {
  private static final long serialVersionUID = 8878741628088898869L;

  @Id
  @Column(name = "rte_lngRouteID")
  private Integer id;

  @Column(name = "rte_strID")
  private String name;

  @Column(name = "rte_StartFacCode")
  private String startingFacilityCode;

  @Column(name = "rte_Desc")
  private String description;

}
