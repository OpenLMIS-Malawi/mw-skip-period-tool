package mw.gov.health.lmis.migration.tool.config;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import lombok.Getter;
import lombok.Setter;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderNumberConfiguration;

import java.time.LocalDate;
import java.util.TimeZone;

@Getter
@Setter
public class ToolParameters {
  private LocalDate startDate = LocalDate.now().minusYears(5);
  private LocalDate endDate = LocalDate.now().with(lastDayOfMonth());
  private Integer numberOfPeriodsToAverage = 2;
  private String creator = "scm";
  private String requestedQuantityExplanation = "transferred from supply manager";
  private TimeZone timeZone = TimeZone.getTimeZone("CAT");
  private OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration(
      "O", true, false, false
  );

  public void setTimeZone(String timeZone) {
    this.timeZone = TimeZone.getTimeZone(timeZone);
  }

  public void setStartDate(String date) {
    startDate = LocalDate.parse(date);
  }

  public void setEndDate(String date) {
    endDate = LocalDate.parse(date);
  }
}
