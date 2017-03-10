package org.openlmis.migration.tool;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class AccessFileValidator implements IValueValidator<Path> {

  @Override
  public void validate(String name, Path value) throws ParameterException {
    if (Files.notExists(value)) {
      throw new ParameterException(
          "The file path passed in parameter: " + name + " does not exist: " + value
      );
    }

    if (!Files.isRegularFile(value)) {
      throw new ParameterException(
          "The value passed in parameter: " + name + " is not a regular file: " + value
      );
    }

    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.mdb");

    if (!matcher.matches(value.getFileName())) {
      throw new ParameterException(
          "The file passed in parameter: " + name + " must have .mdb extension: " + value
      );
    }
  }

}
