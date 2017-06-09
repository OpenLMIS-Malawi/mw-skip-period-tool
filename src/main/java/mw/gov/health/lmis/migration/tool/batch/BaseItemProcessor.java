package mw.gov.health.lmis.migration.tool.batch;

import com.google.common.collect.Lists;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.AccessLevel;
import lombok.Getter;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.ProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.ProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.UserRepository;

import java.util.List;

public abstract class BaseItemProcessor<I, O> implements ItemProcessor<I, O>, InitializingBean {
  @Getter(AccessLevel.PACKAGE)
  private static List<Program> programs;

  @Getter(AccessLevel.PACKAGE)
  private static List<ProcessingPeriod> periods;

  @Getter(AccessLevel.PACKAGE)
  private static User user;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProcessingPeriodRepository periodRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public void afterPropertiesSet() throws Exception {
    synchronized (BaseItemProcessor.class) {
      programs = Lists.newArrayList(programRepository.findAll());
      periods = periodRepository.findInPeriod(
          toolProperties.getParameters().getStartDate().toLocalDate(),
          toolProperties.getParameters().getEndDate().toLocalDate()
      );

      String username = toolProperties.getParameters().getCreator();
      user = userRepository.findByUsername(username);
    }
  }

}
