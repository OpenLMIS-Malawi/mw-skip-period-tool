package org.openlmis.migration.tool;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class App {
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  /**
   * The application start method.
   *
   * @param args a list of arguments
   */
  public static void main(String[] args) {
    Optional<Arguments> parsed = parse(args);
    parsed.ifPresent(arguments -> System.out.println(arguments.getFile()));
  }

  private static Optional<Arguments> parse(String... args) {
    Arguments arguments = new Arguments();
    JCommander commander = new JCommander();

    commander.addObject(arguments);
    commander.setProgramName("java -jar scm-migration-tool.jar");

    try {
      commander.parse(args);
      return Optional.of(arguments);
    } catch (ParameterException exp) {
      LOGGER.error(exp.getMessage());
      commander.usage();

      return Optional.empty();
    }
  }

}
