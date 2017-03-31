package mw.gov.health.lmis.migration.tool.config;

import net.ucanaccess.jdbc.UcanaccessDriver;

import lombok.Getter;
import lombok.Setter;

import java.sql.Driver;

@Getter
@Setter
public class ToolScmDataSourceConfiguration {
  private String file;
  private boolean memory = false;
  private Class<? extends Driver> driverClass = UcanaccessDriver.class;
}
