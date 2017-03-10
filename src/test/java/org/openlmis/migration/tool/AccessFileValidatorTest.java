package org.openlmis.migration.tool;

import com.beust.jcommander.ParameterException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AccessFileValidatorTest {
  private static final String NAME = "-af";

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private AccessFileValidator validator = new AccessFileValidator();

  @Test
  public void shouldThrowExceptionIfPathDoesNotExist() throws Exception {
    exception.expect(ParameterException.class);
    exception.expectMessage("does not exist");

    validator.validate(NAME, Paths.get("incorrect_file_path"));
  }

  @Test
  public void shouldThrowExceptionIfPathIsNotFile() throws Exception {
    exception.expect(ParameterException.class);
    exception.expectMessage("is not a regular file");

    validator.validate(NAME, Paths.get(System.getProperty("user.home")));
  }

  @Test
  public void shouldThrowExceptionIfPathhasIncorrectExtension() throws Exception {
    exception.expect(ParameterException.class);
    exception.expectMessage("must have .mdb extension");

    Path path = Paths.get(getClass().getResource("/test.txt").toURI());
    validator.validate(NAME, path);
  }

  @Test
  public void shouldNotThrowExceptionIfPathRepresentAccessFile() throws Exception {
    Path path = Paths.get(getClass().getResource("/test.mdb").toURI());
    validator.validate(NAME, path);
  }
}