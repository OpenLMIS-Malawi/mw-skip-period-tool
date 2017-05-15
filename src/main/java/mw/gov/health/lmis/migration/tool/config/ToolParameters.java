package mw.gov.health.lmis.migration.tool.config;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import lombok.Getter;
import lombok.Setter;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderNumberConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

@Getter
@Setter
public class ToolParameters {
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");

  private Date startDate = Date.from(
      LocalDate.now().atStartOfDay().minusYears(5).with(firstDayOfMonth()).toInstant(ZoneOffset.UTC)
  );
  private Date endDate = Date.from(
      LocalDate.now().atTime(23, 59, 59).with(lastDayOfMonth()).toInstant(ZoneOffset.UTC)
  );
  private Integer numberOfPeriodsToAverage = 2;
  private String creator = "scm";
  private String requestedQuantityExplanation = "transferred from supply manager";
  private String timeZone = "CAT";
  private OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration(
      "O", true, false, false
  );

  public void setStartDate(String date) throws ParseException {
    startDate = dateFormat.parse(date);
  }

  public void setEndDate(String date) throws ParseException {
    endDate = dateFormat.parse(date);
  }
}
