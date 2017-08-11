package mw.gov.health.lmis.skip.period.tool.batch;

import com.google.common.collect.Lists;

import mw.gov.health.lmis.skip.period.tool.config.ToolParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import mw.gov.health.lmis.skip.period.tool.config.ToolProperties;
import mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.repository.ProcessingPeriodRepository;
import mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.repository.ProgramRepository;
import mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
final class AppBatchContext implements InitializingBean {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppBatchContext.class);

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProcessingPeriodRepository periodRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Getter
  private List<Program> programs;

  @Getter
  private List<ProcessingPeriod> periods;

  @Getter
  private User user;

  @Override
  public void afterPropertiesSet() {
    LOGGER.info("Initialize batch context...");
    TimeZone.setDefault(toolProperties.getParameters().getTimeZone());

    programs = Lists.newArrayList(programRepository.findAll());
    LOGGER.info("Found {} programs", programs.size());

    ToolParameters parameters = toolProperties.getParameters();

    if (null != parameters.getPrograms()) {
      programs = programs
              .stream()
              .filter(program -> parameters.getPrograms().contains(program.getCodeValue()))
              .collect(Collectors.toList());
      LOGGER.info("Select {} programs based on configuration", programs.size());
    }

    LocalDate startDate = parameters.getStartDate();
    LocalDate endDate = parameters.getEndDate();
    periods = periodRepository.findInPeriod(startDate, endDate);
    LOGGER.info("Found {} periods between {} and {}", periods.size(), startDate, endDate);

    String username = parameters.getCreator();
    user = userRepository.findByUsername(username);

    if (null == user) {
      throw new IllegalStateException("Can't find user with username: " + username);
    } else {
      LOGGER.info("Found user with username: {}", username);
    }

    LOGGER.info("Initialized batch context...");
  }

}
