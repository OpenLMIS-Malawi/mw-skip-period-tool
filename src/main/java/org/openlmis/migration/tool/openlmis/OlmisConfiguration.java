package org.openlmis.migration.tool.openlmis;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.IMPLICIT_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.PHYSICAL_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.dialect.PostgreSQL94Dialect;
import org.openlmis.migration.tool.Arguments;
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
  PlatformTransactionManager olmisTransactionManager(Arguments arguments) {
    return new JpaTransactionManager(olmisEntityManagerFactory(arguments));
  }

  @Bean
  EntityManager olmisEntityManager(Arguments arguments) {
    return olmisEntityManagerFactory(arguments).createEntityManager();
  }

  @Bean
  EntityManagerFactory olmisEntityManagerFactory(Arguments arguments) {
    return olmisEntityManagerFactoryBean(arguments).getObject();
  }

  /**
   * Declare the SCMgr entity manager factory.
   */
  @Bean
  LocalContainerEntityManagerFactoryBean olmisEntityManagerFactoryBean(Arguments arguments) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactory.setDataSource(olmisDataSource(arguments));
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
  DataSource olmisDataSource(Arguments arguments) {
    // TODO: make it configurable. The question is how much?
    Properties connectionProperties = new Properties();
    connectionProperties.put("stringtype", "unspecified");

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(Driver.class.getName());
    dataSource.setUrl(generateUrl(arguments));
    dataSource.setUsername(defaultIfBlank(arguments.getUsername(), "postgres"));
    dataSource.setPassword(defaultIfBlank(arguments.getPassword(), "p@ssw0rd"));
    dataSource.setConnectionProperties(connectionProperties);

    return dataSource;
  }

  private String generateUrl(Arguments arguments) {
    String host = defaultIfBlank(arguments.getHost(), "localhost");
    Integer port = defaultIfNull(arguments.getPort(), 5432);
    String database = defaultIfBlank(arguments.getDatabase(), "open_lmis");

    return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
  }

}
