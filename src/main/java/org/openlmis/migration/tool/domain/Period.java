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
@Entity(name = "Period")
public class Period implements Serializable {
  private static final long serialVersionUID = -3768200184412265443L;

  @Column(name = "Period_Name")
  private String name;

  @Id
  @Column(name = "Period_Code")
  private Short code;

  @Column(name = "Month")
  private Boolean singleMonth;

  @Column(name = "Beg_Month")
  private String beginMonth;

  @Column(name = "End_Month")
  private String endMonth;

  @Column(name = "intMinMonths")
  private Integer numberOfMonths;

  @Column(name = "Period_Name_Arabic")
  private String arabicName;

  @Column(name = "Beg_Month_Arabic")
  private String arabicBeginMonth;

  @Column(name = "End_Month_Arabic")
  private String arabicEndMonth;

}
