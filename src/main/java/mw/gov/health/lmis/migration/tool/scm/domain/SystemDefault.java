package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

import java.util.Date;

public class SystemDefault extends BaseEntity {

  public SystemDefault(Row row) {
    super(row);
  }

  public String getSerialNumber() {
    return getString("Serial_No");
  }

  public String getReportTitle1() {
    return getString("Report_Title_1");
  }

  public String getReportTitle2() {
    return getString("Report_Title_2");
  }

  public String getReportTitle3() {
    return getString("Report_Title_3");
  }

  public Short getNumberOfPeriodsToAverage() {
    return getShort("MonthsToSample");
  }

  public String getExportPath() {
    return getString("Export_Path");
  }

  public Date getCurrentMonth() {
    return getDate("CurrentMonth");
  }

  public String getVersion() {
    return getString("Db_Version");
  }

  public Boolean getQuarterly() {
    return getBoolean("blnQuarterly");
  }

  public Short getReportingPeriod() {
    return getShort("dp_intMonths");
  }

  public Boolean getDrpPortionLogistics2000() {
    return getBoolean("sys_fUseDRP");
  }

  public Boolean getLmisPortionLogistics2000() {
    return getBoolean("sys_fUseLMIS");
  }

  public Integer getCalculationLevel() {
    return getInt("sys_intCalcType");
  }

  public String getMeasureUnit() {
    return getString("sys_strMeasure");
  }

  public String getMainFacility() {
    return getString("sys_strDRPMain");
  }

  public Integer getLastReportNumberOfMonths() {
    return getInt("sys_intLastRepot");
  }

  public Short getLanguage() {
    return getShort("sys_intLanguage");
  }

  public Boolean getUseServiceStastics() {
    return getBoolean("sys_fUseServiceStatistics");
  }

  public Boolean getUseQuantityOnOrder() {
    return getBoolean("sys_fUseQtyOnOrder");
  }

  public Boolean getUseNewUsersOnly() {
    return getBoolean("sys_fUseNewUsersOnly");
  }

  public Boolean getUsePurposeOfUse() {
    return getBoolean("sys_fUsePurposeofUse");
  }

  public Boolean getUseQuantityOnHand() {
    return getBoolean("sys_fUseQtyOnHand");
  }

  public Boolean getUseCategoryLevelMinMax() {
    return getBoolean("sys_fUseCatMinMax");
  }

  public Boolean getUseOverrideZeroQuantity() {
    return getBoolean("sys_fUseOverrideRQD");
  }

  public Short getTrackingFacilities() {
    return getShort("sys_intTracking");
  }

  public Date getStartReportingDate() {
    return getDate("sys_dtmStart");
  }

  public Short getMultiTierRole() {
    return getShort("Sys_intMultiTierRole");
  }

  public Boolean getUseTurnOnMtChangeLogging() {
    return getBoolean("sys_fUseTrackMTChangeLog");
  }

  public Double getDefaultFacilityMin() {
    return getDouble("sys_dblDefaultFacilityMin");
  }

  public Double getDefaultFacilityMax() {
    return getDouble("sys_dblDefaultFacilityMax");
  }

  public Double getMultipleComments() {
    return getDouble("sys_fUseTrackMultiComments");
  }

  public Double getMultipleAdjustments() {
    return getDouble("sys_fUseTrackMultiAdj");
  }
}
