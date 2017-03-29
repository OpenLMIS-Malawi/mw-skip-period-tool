package mw.gov.health.lmis.migration.tool.scm.domain;

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
@Entity(name = "tblCategoryProductJoin")
public class CategoryProductJoin implements Serializable {
  private static final long serialVersionUID = -2669236807103209835L;

  @Id
  @Column(name = "ID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "lngCategoryID")
  private Program program;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "strProductID", referencedColumnName = "strProductID")
  private Product product;

  @Column(name = "intOrder")
  private Integer order;

  @Column(name = "intDEOrder")
  private Integer deOrder;
}
