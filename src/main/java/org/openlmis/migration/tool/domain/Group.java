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
@Entity(name = "Type_Group")
public class Group implements Serializable {
  private static final long serialVersionUID = -9214897561997879259L;

  @Id
  @Column(name = "Type_Group_Name")
  private String name;

  @Column(name = "tgp_guidID", columnDefinition = "character")
  private String id;

}
