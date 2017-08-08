package mw.gov.health.lmis.skip.period.tool;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
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

  @Bean
  CommandLineRunner commandLineRunner(JobLauncher launcher, Job job) {
    return args -> {
      launcher.run(job, new JobParameters());
    };
  }

}
