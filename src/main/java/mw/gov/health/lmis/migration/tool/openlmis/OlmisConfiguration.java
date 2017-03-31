package mw.gov.health.lmis.migration.tool.openlmis;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.IMPLICIT_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.PHYSICAL_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import mw.gov.health.lmis.migration.tool.config.ToolOlmisConfiguration;
import mw.gov.health.lmis.migration.tool.config.ToolOlmisDataSourceConfiguration;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "mw.gov.health.lmis.migration.tool.openlmis",
    entityManagerFactoryRef = "olmisEntityManagerFactory",
    transactionManagerRef = "olmisTransactionManager")
public class OlmisConfiguration {

  /**
   * Declare the SCMgr transaction manager.
   */
  @Bean
  PlatformTransactionManager olmisTransactionManager(ToolProperties properties) {
    return new JpaTransactionManager(olmisEntityManagerFactory(properties));
  }

  @Bean
  EntityManager olmisEntityManager(ToolProperties properties) {
    return olmisEntityManagerFactory(properties).createEntityManager();
  }

  @Bean
  EntityManagerFactory olmisEntityManagerFactory(ToolProperties properties) {
    return olmisEntityManagerFactoryBean(properties).getObject();
  }

  /**
   * Declare the SCMgr entity manager factory.
   */
  @Bean
  LocalContainerEntityManagerFactoryBean olmisEntityManagerFactoryBean(ToolProperties properties) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactory.setDataSource(olmisDataSource(properties));
    entityManagerFactory.setPackagesToScan(
        "mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain",
        "mw.gov.health.lmis.migration.tool.openlmis.requisition.domain",
        "mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain"
    );

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true);

    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

    Properties jpaProperties = new Properties();
    jpaProperties.setProperty(
        IMPLICIT_NAMING_STRATEGY, ImplicitNamingStrategyJpaCompliantImpl.class.getName()
    );
    jpaProperties.setProperty(
        PHYSICAL_NAMING_STRATEGY, CustomPhysicalNamingStrategy.class.getName()
    );

    ToolOlmisConfiguration olmis = properties.getConfiguration().getOlmis();

    jpaProperties.setProperty(DIALECT, olmis.getDialect().getName());
    jpaProperties.setProperty(SHOW_SQL, String.valueOf(olmis.isShowSql()));
    jpaProperties.setProperty("hibernate.hbm2ddl.auto", olmis.getHbm2ddl());

    entityManagerFactory.setJpaProperties(jpaProperties);

    return entityManagerFactory;
  }

  /**
   * Declare the SCMgr data source.
   */
  @Bean
  DataSource olmisDataSource(ToolProperties properties) {
    ToolOlmisDataSourceConfiguration dataSource = properties
        .getConfiguration()
        .getOlmis()
        .getDataSource();

    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName(dataSource.getDriverClass().getName());
    ds.setUrl(generateUrl(dataSource));
    ds.setUsername(defaultIfBlank(dataSource.getUsername(), "postgres"));
    ds.setPassword(defaultIfBlank(dataSource.getPassword(), "p@ssw0rd"));
    ds.setConnectionProperties(dataSource.getConnectionProperties());

    return ds;
  }

  private String generateUrl(ToolOlmisDataSourceConfiguration dataSource) {
    String host = defaultIfBlank(dataSource.getHost(), "localhost");
    Integer port = defaultIfNull(dataSource.getPort(), 5432);
    String database = defaultIfBlank(dataSource.getDatabase(), "open_lmis");

    return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
  }

}
