package mw.gov.health.lmis.migration.tool.batch;

import static java.lang.Math.max;
import static java.lang.Runtime.getRuntime;
import static org.springframework.batch.repeat.support.TaskExecutorRepeatTemplate.DEFAULT_THROTTLE_LIMIT;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.boot.autoconfigure.batch.BatchDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import mw.gov.health.lmis.migration.tool.config.ToolBatchConfiguration;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;

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
  public Step migrationStep(StepBuilderFactory stepBuilderFactory,
                            SupplyManagerExtractor reader, OlmisLoader writer,
                            Transformer processor, ToolProperties toolProperties)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    ToolBatchConfiguration batchProperties = toolProperties
        .getConfiguration()
        .getBatch();

    return stepBuilderFactory
        .get("migrationStep")
        .<Main, List<Requisition>>chunk(batchProperties.getChunk())
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .skipPolicy(batchProperties.getSkipPolicy().newInstance())
        .listener(new SupplyManagerExtractListener())
        .listener(new TransformListener())
        .listener(new OlmisLoadListener())
        .taskExecutor(new SimpleAsyncTaskExecutor())
        .throttleLimit(max(DEFAULT_THROTTLE_LIMIT, getRuntime().availableProcessors() - 1))
        .build();
  }

  /**
   * Configure Spring Batch Job that will transform {@link Main} object into {@link Requisition}.
   */
  @Bean
  public Job migrationJob(JobBuilderFactory jobBuilderFactory, Step migrationStep) {
    return jobBuilderFactory
        .get("migrationJob")
        .incrementer(new RunIdIncrementer())
        .flow(migrationStep)
        .end()
        .build();
  }

}
