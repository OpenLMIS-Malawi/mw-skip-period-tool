package mw.gov.health.lmis.migration.tool.config;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RequisitionTemplateConfiguration {
  private String program;
  private Integer numberOfPeriodsToAverage;
  private Map<String, RequisitionTemplateColumnConfiguration> columns = Maps.newHashMap();
}
