package org.openlmis.migration.tool.openlmis;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.IMPLICIT_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.PHYSICAL_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.dialect.PostgreSQL94Dialect;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "org.openlmis.migration.tool.openlmis",
    entityManagerFactoryRef = "olmisEntityManagerFactory",
    transactionManagerRef = "olmisTransactionManager")
public class OlmisConfiguration {

  /**
   * Declare the SCMgr transaction manager.
   */
  @Bean
  PlatformTransactionManager olmisTransactionManager() {
    return new JpaTransactionManager(olmisEntityManagerFactory());
  }

  @Bean
  EntityManager olmisEntityManager() {
    return olmisEntityManagerFactory().createEntityManager();
  }

  @Bean
  EntityManagerFactory olmisEntityManagerFactory() {
    return olmisEntityManagerFactoryBean().getObject();
  }

  /**
   * Declare the SCMgr entity manager factory.
   */
  @Bean
  LocalContainerEntityManagerFactoryBean olmisEntityManagerFactoryBean() {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactory.setDataSource(olmisDataSource());
    entityManagerFactory.setPackagesToScan(
        "org.openlmis.migration.tool.openlmis.fulfillment.domain",
        "org.openlmis.migration.tool.openlmis.requisition.domain",
        "org.openlmis.migration.tool.openlmis.referencedata.domain"
    );

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true);

    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

    Properties properties = new Properties();
    properties.setProperty(
        IMPLICIT_NAMING_STRATEGY, ImplicitNamingStrategyJpaCompliantImpl.class.getName()
    );
    properties.setProperty(PHYSICAL_NAMING_STRATEGY, CustomPhysicalNamingStrategy.class.getName());
    properties.setProperty(DIALECT, PostgreSQL94Dialect.class.getName());
    properties.setProperty(SHOW_SQL, "false");
    properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

    entityManagerFactory.setJpaProperties(properties);

    return entityManagerFactory;
  }

  /**
   * Declare the SCMgr data source.
   */
  @Bean
  DataSource olmisDataSource() {
    // TODO: make it configurable. The question is how much?
    Properties connectionProperties = new Properties();
    connectionProperties.put("stringtype", "unspecified");

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(Driver.class.getName());
    dataSource.setUrl("jdbc:postgresql://localhost:5432/open_lmis_scm");
    dataSource.setUsername("postgres");
    dataSource.setPassword("p@ssw0rd");
    dataSource.setConnectionProperties(connectionProperties);

    return dataSource;
  }

}
