package mw.gov.health.lmis.migration.tool.scm;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

import net.ucanaccess.jdbc.UcanaccessDriver;

import org.hibernate.dialect.H2Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import mw.gov.health.lmis.migration.tool.Arguments;

import java.util.Properties;

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
  PlatformTransactionManager scmTransactionManager(Arguments arguments) {
    return new JpaTransactionManager(scmEntityManagerFactory(arguments).getObject());
  }

  /**
   * Declare the SCMgr entity manager factory.
   */
  @Bean
  @Primary
  LocalContainerEntityManagerFactoryBean scmEntityManagerFactory(Arguments arguments) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactory.setDataSource(scmDataSource(arguments));
    entityManagerFactory.setPackagesToScan("mw.gov.health.lmis.migration.tool.scm.domain");

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
   * Declare the SCMgr data source.
   */
  @Bean
  @Primary
  DataSource scmDataSource(Arguments arguments) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(UcanaccessDriver.class.getName());
    dataSource.setUrl("jdbc:ucanaccess://" + arguments.getFile() + ";memory=false");

    return dataSource;
  }

}
