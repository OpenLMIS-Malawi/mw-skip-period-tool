package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Properties;

@Getter
@Setter
public class ToolMapping {
  private List<ToolProgramMapping> programs;
  private Properties facilities;
  private Properties products;
  private Properties stockAdjustmentReasons;

}
