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
@Entity(name = "Program")
public class Program implements Serializable {
  private static final long serialVersionUID = 3193225110458737602L;

  @Column(name = "Program_Name")
  private String name;

  @Id
  @Column(name = "Program_ID")
  private Integer id;

  @Column(name = "intOrder")
  private Integer order;

  @Column(name = "ParentId")
  private Integer parentId;

}
