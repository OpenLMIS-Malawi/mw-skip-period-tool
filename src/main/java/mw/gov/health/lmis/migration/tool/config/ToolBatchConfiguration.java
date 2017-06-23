package mw.gov.health.lmis.migration.tool.config;

import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolBatchConfiguration {
  private Integer chunk = 10;
  private Class<? extends SkipPolicy> skipPolicy = AlwaysSkipItemSkipPolicy.class;
  private boolean migration = true;
  private boolean removeDuplicates = true;
  private boolean skipPeriods = true;
}
