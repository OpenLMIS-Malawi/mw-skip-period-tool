package mw.gov.health.lmis.migration.tool.scm;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

@Component
public class ScmDatabaseHandler {

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Get access to access database.
   */
  public Database getDatabase() {
    File accessFile = toolProperties.getConfiguration().getAccessFile();
    TimeZone timeZone = toolProperties.getParameters().getTimeZone();

    try {
      return new DatabaseBuilder()
          .setFile(accessFile)
          .setReadOnly(true)
          .setTimeZone(timeZone)
          .open();
    } catch (IOException exp) {
      throw new IllegalStateException("Can't open database: " + accessFile, exp);
    }
  }
}
