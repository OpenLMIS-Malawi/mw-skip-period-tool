package mw.gov.health.lmis.migration.tool.scm.domain;

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
@Entity(name = "tblDistributionLevels")
public class DistributionLevel implements Serializable {
  private static final long serialVersionUID = 8331342174021534104L;

  @Column(name = "dl_strLevelName")
  private String name;

  @Column(name = "dl_intLevelRanking")
  private Short level;

  @Id
  @Column(name = "dl_ID")
  private Integer id;

  @Column(name = "dl_fSDP")
  private Boolean distributeProductToConsumer;

  @Column(name = "dl_fWarehouse")
  private Boolean warehouse;

}
