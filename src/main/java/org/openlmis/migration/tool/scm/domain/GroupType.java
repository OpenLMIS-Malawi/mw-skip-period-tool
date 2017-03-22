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
@Entity(name = "Type_Group_Types")
public class GroupType implements Serializable {
  private static final long serialVersionUID = -4688468520402907235L;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "Type_Group_Name")
  private Group group;

  @Column(name = "Type_Name")
  private String name;

  @Id
  @Column(name = "tgt_guidID", columnDefinition = "character")
  private String id;

}
