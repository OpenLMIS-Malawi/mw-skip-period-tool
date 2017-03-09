package org.openlmis.migration.tool;

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
      validateValueWith = FileExistsValidator.class
  )
  private Path file;

}
