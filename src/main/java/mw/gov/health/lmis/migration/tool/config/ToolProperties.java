package mw.gov.health.lmis.migration.tool.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tool")
public class ToolProperties {
  private ToolParameters parameters = new ToolParameters();
  private ToolConfiguration configuration = new ToolConfiguration();
  private ToolMapping mapping = new ToolMapping();
  private ToolTableNames tableNames = new ToolTableNames();
  private ToolExclude exclude = new ToolExclude();
}
