package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolConfiguration {
  private ToolOlmisConfiguration olmis = new ToolOlmisConfiguration();
  private ToolBatchConfiguration batch = new ToolBatchConfiguration();
}
