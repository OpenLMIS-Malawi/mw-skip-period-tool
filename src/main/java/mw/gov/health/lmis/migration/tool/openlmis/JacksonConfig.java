package mw.gov.health.lmis.migration.tool.openlmis;

import com.bedatadriven.jackson.datatype.jts.JtsModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public JtsModule jtsModule() {
    return new JtsModule();
  }

}
