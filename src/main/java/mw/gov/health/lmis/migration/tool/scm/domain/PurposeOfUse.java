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
@Entity(name = "tblPurposeofUse")
public class PurposeOfUse implements Serializable {
  private static final long serialVersionUID = 8110238667568151682L;

  @Id
  @Column(name = "pur_lngID")
  private Integer id;

  @Column(name = "pur_strName")
  private String name;

  @Column(name = "pur_strDescription")
  private String description;

  @Column(name = "pur_fActive")
  private Boolean active;

}
