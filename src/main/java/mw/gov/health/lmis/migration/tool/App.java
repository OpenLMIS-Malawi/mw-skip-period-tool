package mw.gov.health.lmis.migration.tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

  /**
   * The application start method. If passed arguments are okay it runs with spring context.
   * Otherwise it transform error message and information how to use the application.
   *
   * @param args a list of arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
