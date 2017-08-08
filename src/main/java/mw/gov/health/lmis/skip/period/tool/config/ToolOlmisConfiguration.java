package mw.gov.health.lmis.skip.period.tool.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.spatial.dialect.postgis.PostgisDialect;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolOlmisConfiguration {
  private Class<? extends Dialect> dialect = PostgisDialect.class;
  private boolean showSql = false;
  private ToolOlmisDataSourceConfiguration dataSource = new ToolOlmisDataSourceConfiguration();
}
