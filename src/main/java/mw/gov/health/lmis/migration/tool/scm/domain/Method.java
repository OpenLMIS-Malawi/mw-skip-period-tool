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
@Entity(name = "tblMethods")
public class Method implements Serializable {
  private static final long serialVersionUID = 1226573190854673900L;

  @Id
  @Column(name = "mth_lngMethodID")
  private Integer id;

  @Column(name = "mth_strMethodName")
  private String name;

  @Column(name = "mth_strNote")
  private String note;

  @Column(name = "mth_sngCYP")
  private Double cypFactor;

}
