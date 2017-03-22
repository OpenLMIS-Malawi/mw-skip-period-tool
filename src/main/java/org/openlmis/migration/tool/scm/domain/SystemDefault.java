package org.openlmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "Sys_Defaults")
public class SystemDefault implements Serializable {
  private static final long serialVersionUID = -6205626432288325812L;

  @Id
  @Column(name = "Serial_No")
  private String serialNumber;

  @Column(name = "Report_Title_1")
  private String reportTitle1;

  @Column(name = "Report_Title_2")
  private String reportTitle2;

  @Column(name = "Report_Title_3")
  private String reportTitle3;

  @Column(name = "MonthsToSample")
  private Short numberOfPeriodsToAverage;

  @Column(name = "Export_Path")
  private String exportPath;

  @Column(name = "CurrentMonth")
  private LocalDateTime currentMonth;

  @Column(name = "Db_Version")
  private String version;

  @Column(name = "blnQuarterly")
  private Boolean quarterly;

  @Column(name = "dp_intMonths")
  private Short reportingPeriod;

  @Column(name = "sys_fUseDRP")
  private Boolean drpPortionLogistics2000;

  @Column(name = "sys_fUseLMIS")
  private Boolean lmisPortionLogistics2000;

  @Column(name = "sys_intCalcType")
  private Integer calculationLevel;

  @Column(name = "sys_strMeasure")
  private String measureUnit;

  @Column(name = "sys_strDRPMain")
  private String mainFacility;

  @Column(name = "sys_intLastRepot")
  private Integer lastReportNumberOfMonths;

  @Column(name = "sys_intLanguage")
  private Short language;

  @Column(name = "sys_fUseServiceStatistics")
  private Boolean useServiceStastics;

  @Column(name = "sys_fUseQtyOnOrder")
  private Boolean useQuantityOnOrder;

  @Column(name = "sys_fUseNewUsersOnly")
  private Boolean useNewUsersOnly;

  @Column(name = "sys_fUsePurposeofUse")
  private Boolean usePurposeOfUse;

  @Column(name = "sys_fUseQtyOnHand")
  private Boolean useQuantityOnHand;

  @Column(name = "sys_fUseCatMinMax")
  private Boolean useCategoryLevelMinMax;

  @Column(name = "sys_fUseOverrideRQD")
  private Boolean useOverrideZeroQuantity;

  @Column(name = "sys_intTracking")
  private Short trackingFacilities;

  @Column(name = "sys_dtmStart")
  private LocalDateTime startReportingDate;

  @Column(name = "Sys_intMultiTierRole")
  private Short multiTierRole;

  @Column(name = "sys_fUseTrackMTChangeLog")
  private Boolean useTurnOnMtChangeLogging;

  @Column(name = "sys_dblDefaultFacilityMin")
  private Double defaultFacilityMin;

  @Column(name = "sys_dblDefaultFacilityMax")
  private Double defaultFacilityMax;

  @Column(name = "sys_fUseTrackMultiComments")
  private Double multipleComments;

  @Column(name = "sys_fUseTrackMultiAdj")
  private Double multipleAdjustments;

}
