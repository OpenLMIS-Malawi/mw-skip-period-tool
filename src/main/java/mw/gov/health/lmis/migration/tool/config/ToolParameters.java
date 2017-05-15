package mw.gov.health.lmis.migration.tool.config;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import lombok.Getter;
import lombok.Setter;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderNumberConfiguration;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
public class ToolParameters {
  private ZonedDateTime startDate = LocalDate.now().atStartOfDay(UTC)
      .minusYears(5).with(firstDayOfMonth());
  private ZonedDateTime endDate = LocalDate.now().atTime(23, 59, 59)
      .with(lastDayOfMonth()).atZone(UTC);
  private Integer numberOfPeriodsToAverage = 2;
  private String creator = "scm";
  private String requestedQuantityExplanation = "transferred from supply manager";
  private String timeZone = "CAT";
  private OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration(
      "O", true, false, false
  );

  public void setStartDate(String date) throws ParseException {
    startDate = LocalDate.parse(date).atStartOfDay(UTC);
  }

  public void setEndDate(String date) throws ParseException {
    endDate = LocalDate.parse(date).atTime(23, 59, 59).atZone(UTC);
  }
}
