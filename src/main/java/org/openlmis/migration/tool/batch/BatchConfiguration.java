package org.openlmis.migration.tool.batch;

import org.openlmis.migration.tool.openlmis.requisition.domain.Requisition;
import org.openlmis.migration.tool.scm.domain.Main;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.boot.autoconfigure.batch.BatchDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Bean
  public AppBatchConfigurer batchConfigurer() {
    return new AppBatchConfigurer();
  }

  @Bean
  public BatchDatabaseInitializer batchDatabaseInitializer(ApplicationContext applicationContext,
                                                           BatchProperties batchProperties) {
    return new AppBatchDatabaseInitializer(batchConfigurer(), applicationContext, batchProperties);
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
        .<Main, List<Requisition>>chunk(10)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .skipPolicy(new AlwaysSkipItemSkipPolicy())
        .listener(new MainReadListener())
        .listener(new MainProcessListener())
        .listener(new RequisitionWriteListener())
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

}
