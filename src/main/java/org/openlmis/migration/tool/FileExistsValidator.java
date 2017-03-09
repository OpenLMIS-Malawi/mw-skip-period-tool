package org.openlmis.migration.tool;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileExistsValidator implements IValueValidator<Path> {

  @Override
  public void validate(String name, Path value) throws ParameterException {
    if (Files.notExists(value)) {
      throw new ParameterException(
          "The file path passed in parameter: " + name + " does not exist: " + value
      );
    }
  }

}
