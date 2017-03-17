package org.openlmis.migration.tool.domain;

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
@Entity(name = "tlkCategoryChain")
public class CategoryChain implements Serializable {
  private static final long serialVersionUID = -2832247471947637244L;

  @EmbeddedId
  private ComplexId id;

  @Embeddable
  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @EqualsAndHashCode
  public static class ComplexId implements Serializable {
    private static final long serialVersionUID = -2402753660105199351L;

    @Column(name = "lngParentID")
    private Integer parentId;

    @Column(name = "lngChildID")
    private Integer childId;

  }

}
