package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdjustmentType {
  private String code;
  private String name;
  private Boolean negative;
  private Boolean userCanEnter;
  private Boolean active;
  private String id;

  @Override
  public String toString() {
    return code;
  }
  
}
