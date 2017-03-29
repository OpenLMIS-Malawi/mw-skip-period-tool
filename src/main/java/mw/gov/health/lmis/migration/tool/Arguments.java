package mw.gov.health.lmis.migration.tool;

import com.beust.jcommander.Parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;

@NoArgsConstructor
@Getter
@Setter
public class Arguments {

  @Parameter(
      names = { "--access-file", "-af" },
      description = "Path to an access database file",
      required = true,
      validateValueWith = AccessFileValidator.class
  )
  private Path file;

  @Parameter(
      names = {"--target-host", "-th"},
      description = "OpenLMIS database host address"
  )
  private String host;

  @Parameter(
      names = {"--target-port", "-tp"},
      description = "OpenLMIS database port number"
  )
  private Integer port;

  @Parameter(
      names = {"--target-database", "-td"},
      description = "OpenLMIS database name"
  )
  private String database;

  @Parameter(
      names = {"--target-user", "-tu"},
      description = "OpenLMIS database username"
  )
  private String username;

  @Parameter(
      names = {"--target-password", "-tpass"},
      description = "Password for the given username of OpenLMIS database"
  )
  private String password;

}
