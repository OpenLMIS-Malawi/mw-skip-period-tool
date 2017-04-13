package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderNumberConfiguration;

@Getter
@Setter
public class ToolParameters {
  private Interval interval = new Interval();
  private String creator = "scm";
  private String requestedQuantityExplanation = "transferred from supply manager";
  private String timeZone = "CAT";
  private OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration(
      "O", true, false, false
  );

  @Getter
  @Setter
  public static class Interval {
    private Integer days = 0;
    private Integer months = 0;
    private Integer years = 5;
  }
}
