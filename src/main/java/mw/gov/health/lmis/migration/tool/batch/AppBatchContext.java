package mw.gov.health.lmis.migration.tool.batch;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import mw.gov.health.lmis.migration.tool.scm.repository.ItemAccessRepository;

import java.util.List;
import java.util.TimeZone;

public abstract class AppBatchContext implements InitializingBean {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppBatchContext.class);

  private static final Object lock = new Object();
  private static boolean initialized = false;

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

  @Autowired
  private ItemAccessRepository itemRepository;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (!initialized) {
      synchronized (lock) {
        if (!initialized) {
          LOGGER.info("Initialize batch context...");
          TimeZone.setDefault(toolProperties.getParameters().getTimeZone());

          programs = Lists.newArrayList(programRepository.findAll());
          periods = periodRepository.findInPeriod(
              toolProperties.getParameters().getStartDate(),
              toolProperties.getParameters().getEndDate()
          );

          String username = toolProperties.getParameters().getCreator();
          user = userRepository.findByUsername(username);

          itemRepository.init();

          initialized = true;
          LOGGER.info("Initialized batch context...");
        }
      }
    }
  }

}
