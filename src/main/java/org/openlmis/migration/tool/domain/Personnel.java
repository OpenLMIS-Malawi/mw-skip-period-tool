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
@Entity(name = "tblPersonnel")
public class Personnel implements Serializable {
  private static final long serialVersionUID = 53735825836081267L;

  @Id
  @Column(name = "per_lngPersonID")
  private Integer id;

  @Column(name = "per_strLastName")
  private String lastName;

  @Column(name = "per_strFirstName")
  private String firstName;

}
