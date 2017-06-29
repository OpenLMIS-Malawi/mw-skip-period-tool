package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolExcludeForm {
  private String facility;
  private String period;
  private String program;

  /**
   * Checks if this exclude form entry match the given facility, period and program.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  public boolean match(String facility, String period, String program) {
    if (null == this.facility && null == this.period && null == this.program) {
      return false;
    }

    if (null == this.facility && null == this.period) {
      return this.program.equals(program);
    }

    if (null == this.facility && null == this.program) {
      return this.period.equals(period);
    }

    if (null == this.period && null == this.program) {
      return this.facility.equals(facility);
    }

    if (null == this.facility) {
      return this.program.equals(program) && this.period.equals(period);
    }

    if (null == this.period) {
      return this.facility.equals(facility) && this.program.equals(program);
    }

    if (null == this.program) {
      return this.facility.equals(facility) && this.period.equals(period);
    }

    return this.facility.equals(facility)
        && this.period.equals(period)
        && this.program.equals(program);
  }

}
