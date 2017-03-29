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
@Entity(name = "tblDeliveryProducts")
public class DeliveryProduct implements Serializable {
  private static final long serialVersionUID = 1177995059811625234L;

  @Id
  @Column(name = "itm_lngDelProductID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dst_lngStopID")
  private DeliveryStop stop;

  @Column(name = "dst_lngProductID")
  private Integer productId;

  @Column(name = "dst_lngQty")
  private Integer quantity;

}
