package org.openlmis.migration.tool.domain;

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
@Entity(name = "tblRouteStops")
public class RouteStop implements Serializable {
  private static final long serialVersionUID = 3384969982435036403L;

  @Id
  @Column(name = "stp_lngStopID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rte_lngRouteID")
  private Route route;

  @Column(name = "stp_intStopOrder")
  private Short stopOrder;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "stp_strFacCode", referencedColumnName = "Fac_Code")
  private Facility facilityCode;

  @Column(name = "stp_sngDistancePrev")
  private Double previousStopDistance;

  @Column(name = "stp_sngTimePrev")
  private Double previousStopTime;

  @Column(name = "stp_sngDistanceSup")
  private Double distanceFromSupplyingFacility;

  @Column(name = "stp_sngTimeSup")
  private Double timeFromSupplyingFacility;

}
