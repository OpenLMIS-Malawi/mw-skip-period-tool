package mw.gov.health.lmis.migration.tool.scm;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;

import java.io.IOException;

@Getter
@Component
public class ScmDatabase {
  private final Database database;

  @Autowired
  public ScmDatabase(ToolProperties properties) throws IOException {
    database = DatabaseBuilder.open(properties.getConfiguration().getAccessFile());
  }

}
