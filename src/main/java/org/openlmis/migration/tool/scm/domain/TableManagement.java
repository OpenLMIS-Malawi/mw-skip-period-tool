package org.openlmis.migration.tool.scm.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "tblMTTableManagement")
public class TableManagement implements Serializable {
  private static final long serialVersionUID = -5989915997784315427L;

  @EmbeddedId
  private ComplexId id;

  @Column(name = "mtt_fCanModify")
  private Boolean canModify;

  @Column(name = "mtt_bytCanModify")
  private Short canModifyAsInt;

  @Embeddable
  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class ComplexId implements Serializable {
    private static final long serialVersionUID = -2217701390612351728L;

    @Column(name = "mtt_strTableName")
    private String name;

    @Column(name = "mtt_strTableGroup")
    private String group;

  }

}
