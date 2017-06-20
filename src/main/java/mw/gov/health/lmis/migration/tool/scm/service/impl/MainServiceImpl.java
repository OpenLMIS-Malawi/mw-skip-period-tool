package mw.gov.health.lmis.migration.tool.scm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.service.MainService;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class MainServiceImpl implements MainService {

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public LocalDate getProcessingDate(Main main) {
    ZoneId zoneId = toolProperties.getParameters().getTimeZone().toZoneId();
    return main.getProcessingDate().toInstant().atZone(zoneId).toLocalDate();
  }

}
