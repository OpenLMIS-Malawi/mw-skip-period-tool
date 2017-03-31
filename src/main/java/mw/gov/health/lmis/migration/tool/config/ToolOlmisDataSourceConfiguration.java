package mw.gov.health.lmis.migration.tool.config;

import org.postgresql.Driver;

import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

@Getter
@Setter
public class ToolOlmisDataSourceConfiguration {
  private Properties connectionProperties = new Properties();
  private Class<? extends java.sql.Driver> driverClass = Driver.class;
  private String host = "localhost";
  private Integer port = 5432;
  private String database = "open_lmis";
  private String username;
  private String password;
}
