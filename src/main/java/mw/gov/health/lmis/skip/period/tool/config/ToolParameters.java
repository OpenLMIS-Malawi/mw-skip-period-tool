package mw.gov.health.lmis.skip.period.tool.config;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

@Getter
@Setter
public class ToolParameters {
  private LocalDate startDate = LocalDate.now().minusYears(5);
  private LocalDate endDate = LocalDate.now().with(lastDayOfMonth());
  private String creator = "admin";
  private TimeZone timeZone = TimeZone.getTimeZone("Africa/Johannesburg");
  private List<String> facilities = Lists.newArrayList();
  private List<String> programs = Lists.newArrayList();

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
