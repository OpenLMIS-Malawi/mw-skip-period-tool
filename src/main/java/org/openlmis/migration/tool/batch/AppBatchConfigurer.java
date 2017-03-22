package org.openlmis.migration.tool.batch;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public class AppBatchConfigurer implements BatchConfigurer {
  private DefaultBatchConfigurer defaultBatchConfigurer;
  private EmbeddedDatabase dataSource;

  /**
   * Creates a new instance of {@link AppBatchConfigurer} with embedded H2 database.
   */
  public AppBatchConfigurer() {
    dataSource = new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .setName("batch")
        .build();
  }

  @PostConstruct
  public void initialize() {
    defaultBatchConfigurer = new DefaultBatchConfigurer(dataSource);
    defaultBatchConfigurer.initialize();
  }

  @Override
  public JobRepository getJobRepository() {
    return defaultBatchConfigurer.getJobRepository();
  }

  @Override
  public PlatformTransactionManager getTransactionManager() {
    return defaultBatchConfigurer.getTransactionManager();
  }

  @Override
  public JobLauncher getJobLauncher() {
    return defaultBatchConfigurer.getJobLauncher();
  }

  @Override
  public JobExplorer getJobExplorer() {
    return defaultBatchConfigurer.getJobExplorer();
  }

  DataSource getDataSource() {
    return dataSource;
  }
}
