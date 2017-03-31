package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ToolProgramMapping {
  private String code;
  private List<String> categories;
  private List<ToolProgramWarehouseMapping> warehouses;
}
