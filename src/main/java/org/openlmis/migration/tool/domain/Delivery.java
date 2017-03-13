package org.openlmis.migration.tool.domain;

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
@Entity(name = "tblDeliveries")
public class Delivery implements Serializable {
  private static final long serialVersionUID = 349606805754571898L;

  @Id
  @Column(name = "dlv_lngDeliveryID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rte_lngRouteID")
  private Route route;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "trk_lngTruckID")
  private Truck truck;

  @Column(name = "dlv_dtmDelivery")
  private LocalDateTime deliveryDate;

  @Column(name = "dlv_fAccepted")
  private Boolean accepted;

  @Column(name = "dlv_fCompleted")
  private Boolean completed;

  @Column(name = "dlv_fMultiRoute")
  private Boolean multiRoute;

  @Column(name = "dlv_dtmActual")
  private LocalDateTime actualDeliveryDate;

}
