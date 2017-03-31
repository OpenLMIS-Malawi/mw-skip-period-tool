package mw.gov.health.lmis.migration.tool.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolScmConfiguration {
  private Class<? extends Dialect> dialect = H2Dialect.class;
  private boolean showSql = false;
  private String hbm2ddl = "validate";
  private ToolScmDataSourceConfiguration dataSource = new ToolScmDataSourceConfiguration();
}
