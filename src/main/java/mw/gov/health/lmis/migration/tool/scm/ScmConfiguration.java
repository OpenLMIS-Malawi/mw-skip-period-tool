package mw.gov.health.lmis.migration.tool.scm;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.config.ToolScmConfiguration;
import mw.gov.health.lmis.migration.tool.config.ToolScmDataSourceConfiguration;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "mw.gov.health.lmis.migration.tool.scm",
    entityManagerFactoryRef = "scmEntityManagerFactory",
    transactionManagerRef = "scmTransactionManager")
public class ScmConfiguration {

  /**
   * Declare the SCMgr transaction manager.
   */
  @Bean
  @Primary
  PlatformTransactionManager scmTransactionManager(ToolProperties properties) {
    return new JpaTransactionManager(scmEntityManagerFactory(properties));
  }

  @Bean
  EntityManager scmEntityManager(ToolProperties properties) {
    return scmEntityManagerFactory(properties).createEntityManager();
  }

  @Bean
  EntityManagerFactory scmEntityManagerFactory(ToolProperties properties) {
    return scmEntityManagerFactoryBean(properties).getObject();
  }

  /**
   * Declare the SCMgr entity manager factory.
   */
  @Bean
  @Primary
  LocalContainerEntityManagerFactoryBean scmEntityManagerFactoryBean(ToolProperties properties) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();

    ToolScmConfiguration scm = properties.getConfiguration().getScm();

    entityManagerFactory.setDataSource(scmDataSource(properties));
    entityManagerFactory.setPackagesToScan("mw.gov.health.lmis.migration.tool.scm.domain");

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

    Properties jpaProperties = new Properties();
    jpaProperties.setProperty(DIALECT, scm.getDialect().getName());
    jpaProperties.setProperty(SHOW_SQL, String.valueOf(scm.isShowSql()));
    jpaProperties.setProperty(HBM2DDL_AUTO, scm.getHbm2ddl());

    entityManagerFactory.setJpaProperties(jpaProperties);

    return entityManagerFactory;
  }

  /**
   * Declare the SCMgr data source.
   */
  @Bean
  @Primary
  DataSource scmDataSource(ToolProperties properties) {
    ToolScmDataSourceConfiguration configuration = properties
        .getConfiguration()
        .getScm()
        .getDataSource();

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(configuration.getDriverClass().getName());
    dataSource.setUrl(
        "jdbc:ucanaccess://" + configuration.getFile() + ";memory=" + configuration.isMemory()
    );

    return dataSource;
  }

}
