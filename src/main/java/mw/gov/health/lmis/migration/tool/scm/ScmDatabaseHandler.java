package mw.gov.health.lmis.migration.tool.scm;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

@Getter
@Component
public class ScmDatabaseHandler {
  private final Database database;

  /**
   * Create a new instance of this handler.
   */
  @Autowired
  public ScmDatabaseHandler(ToolProperties properties) {
    File accessFile = properties.getConfiguration().getAccessFile();

    try {
      database = new DatabaseBuilder()
          .setFile(accessFile)
          .setReadOnly(true)
          .setTimeZone(TimeZone.getTimeZone(properties.getParameters().getTimeZone()))
          .open();
    } catch (IOException exp) {
      throw new IllegalStateException("Can't open database: " + accessFile, exp);
    }
  }

}
