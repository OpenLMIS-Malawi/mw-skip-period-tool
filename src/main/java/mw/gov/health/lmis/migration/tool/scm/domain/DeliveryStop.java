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
@Entity(name = "tblDeliveryStops")
public class DeliveryStop implements Serializable {
  private static final long serialVersionUID = -2797423931486935782L;

  @Id
  @Column(name = "dst_lngStopID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dlv_lngDeliveryID")
  private Delivery delivery;

  @Column(name = "dlv_strFacCode")
  private String facilityCode;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rte_lngRouteID")
  private Route route;

  @Column(name = "dst_intStopOrder")
  private Integer stopOrder;

  @Column(name = "dst_sngDistance")
  private Double distance;

  @Column(name = "dst_fAccurate")
  private Boolean accurate;

}
