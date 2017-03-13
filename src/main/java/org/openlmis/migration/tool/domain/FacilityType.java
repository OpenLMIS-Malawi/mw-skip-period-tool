package org.openlmis.migration.tool.domain;

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
@Entity(name = "Fac_type")
public class FacilityType implements Serializable {
  private static final long serialVersionUID = 7899402849226580839L;

  @Id
  @Column(name = "Type_Name")
  private String name;

  @Column(name = "Type_Hierarchy")
  private Short level;

  @Column(name = "typ_guidID", columnDefinition = "character")
  private String id;

}
