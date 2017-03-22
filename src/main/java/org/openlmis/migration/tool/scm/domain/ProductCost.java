package org.openlmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@Entity(name = "tblProductCost")
public class ProductCost implements Serializable {
  private static final long serialVersionUID = 2934808257843991369L;

  @Id
  @Column(name = "pc_lngID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "strProductID", referencedColumnName = "strProductID")
  private Product product;

  @Column(name = "pc_datEffective")
  private LocalDateTime effectiveDate;

  @Column(name = "pc_datEnd")
  private LocalDateTime endDate;

  @Column(name = "pc_sngCost")
  private Double cost;

}
