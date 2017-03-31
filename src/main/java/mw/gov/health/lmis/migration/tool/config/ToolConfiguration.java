package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolConfiguration {
  private ToolScmConfiguration scm = new ToolScmConfiguration();
  private ToolOlmisConfiguration olmis = new ToolOlmisConfiguration();
  private ToolBatchConfiguration batch = new ToolBatchConfiguration();
}
