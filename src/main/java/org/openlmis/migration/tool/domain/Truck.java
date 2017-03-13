package org.openlmis.migration.tool.domain;

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
@Entity(name = "tblTrucks")
public class Truck implements Serializable {
  private static final long serialVersionUID = 340687349105648559L;

  @Id
  @Column(name = "trk_lngTruckID")
  private Integer id;

  @Column(name = "trk_strName")
  private String name;

  @Column(name = "trk_strMake")
  private String manufacturer;

  @Column(name = "trk_strModel")
  private String model;

  @Column(name = "trk_intYear")
  private Short yearOfManufacture;

  @Column(name = "trk_lngMaxLoad")
  private Double maxLoad;

  @Column(name = "trk_sngDimHeight")
  private Double cargoAreaHeight;

  @Column(name = "trk_sngDimLength")
  private Double cargoAreaLength;

  @Column(name = "trk_sngDimWidth")
  private Double cargoAreaWidth;

}
