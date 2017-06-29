package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ToolExclude {
  private List<ToolExcludeForm> forms;
  private List<String> products;
}
