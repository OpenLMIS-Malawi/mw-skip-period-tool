package mw.gov.health.lmis.migration.tool.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL94Dialect;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolOlmisConfiguration {
  private Class<? extends Dialect> dialect = PostgreSQL94Dialect.class;
  private boolean showSql = false;
  private ToolOlmisDataSourceConfiguration dataSource = new ToolOlmisDataSourceConfiguration();
}
