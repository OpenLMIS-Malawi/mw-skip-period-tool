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
@Entity(name = "tblDataReportingPeriods")
public class DataReportingPeriod implements Serializable {
  private static final long serialVersionUID = 6697570093182677737L;

  @Column(name = "dp_strPeriodName")
  private String name;

  @Column(name = "dp_intMonths")
  private Short monthsNumber;

  @Id
  @Column(name = "dp_txtTransCode")
  private String translationCode;
}
