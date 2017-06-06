package mw.gov.health.lmis.migration.tool.scm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.service.MainService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

@Service
public class MainServiceImpl implements MainService {

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public LocalDate getProcessingDate(Main main) {
    String timeZoneName = toolProperties.getParameters().getTimeZone();
    TimeZone timeZone = TimeZone.getTimeZone(timeZoneName);
    ZoneId zoneId = timeZone.toZoneId();

    Date processingDate = main.getProcessingDate();
    
    Instant instant = processingDate.toInstant();
    instant = instant.truncatedTo(ChronoUnit.DAYS);

    return instant.atZone(zoneId).toLocalDate();
  }

}
