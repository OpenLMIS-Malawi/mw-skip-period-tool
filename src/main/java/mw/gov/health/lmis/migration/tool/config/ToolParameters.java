package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderNumberConfiguration;

import java.time.Period;

@Getter
@Setter
public class ToolParameters {
  private Interval interval = new Interval();
  private String creator;
  private String requestedQuantityExplanation;
  private String timeZone = "CAT";
  private OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration();

  @Getter
  @Setter
  public class Interval {
    private Integer days;
    private Integer months;
    private Integer years;

    public Period asPeriod() {
      return Period.of(years, months, days);
    }
  }
}
