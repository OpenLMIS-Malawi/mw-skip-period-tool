package org.openlmis.migration.tool;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.util.Optional;

public class App {

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
      System.out.println("ERROR: " + exp.getMessage());
      commander.usage();

      return Optional.empty();
    }
  }

}
