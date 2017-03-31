package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SystemDefault {
  private String serialNumber;
  private String reportTitle1;
  private String reportTitle2;
  private String reportTitle3;
  private Short numberOfPeriodsToAverage;
  private String exportPath;
  private LocalDateTime currentMonth;
  private String version;
  private Boolean quarterly;
  private Short reportingPeriod;
  private Boolean drpPortionLogistics2000;
  private Boolean lmisPortionLogistics2000;
  private Integer calculationLevel;
  private String measureUnit;
  private String mainFacility;
  private Integer lastReportNumberOfMonths;
  private Short language;
  private Boolean useServiceStastics;
  private Boolean useQuantityOnOrder;
  private Boolean useNewUsersOnly;
  private Boolean usePurposeOfUse;
  private Boolean useQuantityOnHand;
  private Boolean useCategoryLevelMinMax;
  private Boolean useOverrideZeroQuantity;
  private Short trackingFacilities;
  private LocalDateTime startReportingDate;
  private Short multiTierRole;
  private Boolean useTurnOnMtChangeLogging;
  private Double defaultFacilityMin;
  private Double defaultFacilityMax;
  private Double multipleComments;
  private Double multipleAdjustments;
}
