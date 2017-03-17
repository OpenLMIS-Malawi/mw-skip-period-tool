package org.openlmis.migration.tool;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

import net.ucanaccess.jdbc.UcanaccessDriver;

import org.hibernate.dialect.H2Dialect;
import org.openlmis.migration.tool.batch.MainProcessor;
import org.openlmis.migration.tool.batch.MainReader;
import org.openlmis.migration.tool.batch.RequisitionWriter;
import org.openlmis.migration.tool.domain.Main;
import org.openlmis.migration.tool.openlmis.requisition.domain.Requisition;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

import javax.sql.DataSource;

@SpringBootApplication
@EnableTransactionManagement
@EnableBatchProcessing
public class AppConfiguration extends DefaultBatchConfigurer {

  /**
   * Here the application starts with spring context.
   */
  @Bean
  public CommandLineRunner commandLineRunner(JobLauncher jobLauncher, Job mainTransformJob) {
    return args -> jobLauncher.run(mainTransformJob, new JobParameters());
  }

  /**
   * Declare the JPA entity manager factory.
   */
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(Arguments arguments) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(UcanaccessDriver.class.getName());
    dataSource.setUrl("jdbc:ucanaccess://" + arguments.getFile() + ";memory=false");

    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactory.setDataSource(dataSource);
    entityManagerFactory.setPackagesToScan("org.openlmis.migration.tool.domain");

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

    Properties properties = new Properties();
    properties.setProperty(DIALECT, H2Dialect.class.getName());
    properties.setProperty(SHOW_SQL, "false");
    properties.setProperty(HBM2DDL_AUTO, "validate");

    entityManagerFactory.setJpaProperties(properties);

    return entityManagerFactory;
  }

  /**
   * Declare the transaction manager.
   */
  @Bean
  public JpaTransactionManager transactionManager(Arguments arguments) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory(arguments).getObject());

    return transactionManager;
  }

  /**
   * PersistenceExceptionTranslationPostProcessor is a bean post processor which adds an advisor
   * to any bean annotated with Repository so that any platform-specific exceptions are caught
   * and then rethrown as one Spring's unchecked data access exceptions (i.e. a subclass of
   * DataAccessException).
   */
  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  /**
   * Configure Spring Batch Step that will read {@link Main} object, convert it into
   * {@link Requisition} object and save it into OpenLMIS database.
   */
  @Bean
  public Step mainTransformStep(StepBuilderFactory stepBuilderFactory,
                                MainReader reader, RequisitionWriter writer,
                                MainProcessor processor) {
    return stepBuilderFactory
        .get("mainTransformStep")
        .<Main, Requisition>chunk(1)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  /**
   * Configure Spring Batch Job that will transform {@link Main} object into {@link Requisition}.
   */
  @Bean
  public Job mainTransformJob(JobBuilderFactory jobBuilderFactory, Step mainTransformStep) {
    return jobBuilderFactory
        .get("mainTransformJob")
        .incrementer(new RunIdIncrementer())
        .flow(mainTransformStep)
        .end()
        .build();
  }

  @Override
  public void setDataSource(DataSource dataSource) {
    // nothing to do here
  }
}
