package mw.gov.health.lmis.migration.tool.batch;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.ProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.ProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.UserRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ItemAccessRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Predicate;

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

  @Autowired
  private ItemAccessRepository itemRepository;

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

    LocalDate startDate = toolProperties.getParameters().getStartDate();
    LocalDate endDate = toolProperties.getParameters().getEndDate();
    periods = periodRepository.findInPeriod(startDate, endDate);
    LOGGER.info("Found {} periods between {} and {}", periods.size(), startDate, endDate);

    String username = toolProperties.getParameters().getCreator();
    user = userRepository.findByUsername(username);

    if (null == user) {
      throw new IllegalStateException("Can't find user with username: " + username);
    } else {
      LOGGER.info("Found user with username: {}", username);
    }

    itemRepository.init();

    LOGGER.info("Initialized batch context...");
  }

  Program findProgram(Predicate<Program> predicate) {
    return programs.stream().filter(predicate).findFirst().orElse(null);
  }

  Program findProgramById(UUID id) {
    return programs.stream().filter(elem -> id.equals(elem.getId())).findFirst().orElse(null);
  }

  Program findProgramByCode(String code) {
    return programs
        .stream()
        .filter(elem -> code.equals(elem.getCode().toString()))
        .findFirst()
        .orElse(null);
  }

  ProcessingPeriod findPeriod(Predicate<ProcessingPeriod> predicate) {
    return periods.stream().filter(predicate).findFirst().orElse(null);
  }

  ProcessingPeriod findPeriodById(UUID id) {
    return periods.stream().filter(elem -> id.equals(elem.getId())).findFirst().orElse(null);
  }
}
