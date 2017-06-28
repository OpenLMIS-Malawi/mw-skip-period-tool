package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

@Getter
@Setter
public class ToolMapping {
  private List<ToolProgramMapping> programs;
  private Map<String, String> facilities;
  private Properties products;
  private Properties stockAdjustmentReasons;
  private Map<Integer, Integer> categoryProductJoins;

  public void setFacilities(Map<String, String> facilities) {
    this.facilities = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    this.facilities.putAll(facilities);
  }

}
