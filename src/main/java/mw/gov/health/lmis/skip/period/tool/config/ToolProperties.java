package mw.gov.health.lmis.skip.period.tool.config;

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
}
