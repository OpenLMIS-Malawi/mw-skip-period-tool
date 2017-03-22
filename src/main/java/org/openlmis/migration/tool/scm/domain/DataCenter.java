package org.openlmis.migration.tool.scm.domain;

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
@Entity(name = "tblMTDataCenters")
public class DataCenter implements Serializable {
  private static final long serialVersionUID = 9100335738244545145L;

  @Id
  @Column(name = "dc_GuidID", columnDefinition = "character")
  private String id;

  @Column(name = "dc_strName")
  private String name;

  @Column(name = "dc_strEmail")
  private String email;

  @Column(name = "dc_fMain")
  private Boolean main;

  @Column(name = "dc_fMyCenter")
  private Boolean myCenter;

}
