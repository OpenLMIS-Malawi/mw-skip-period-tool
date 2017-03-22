package org.openlmis.migration.tool.scm.domain;

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
@Entity(name = "tblDeliveryCrew")
public class DeliveryCrew implements Serializable {
  private static final long serialVersionUID = 6472648987702043939L;

  @Id
  @Column(name = "crw_lngCrewID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dlv_lngDeliveryID")
  private Delivery delivery;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "per_lngPersonID")
  private Personnel personnel;

}
