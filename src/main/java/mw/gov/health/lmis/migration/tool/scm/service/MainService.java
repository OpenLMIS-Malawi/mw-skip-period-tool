package mw.gov.health.lmis.migration.tool.scm.service;

import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.time.LocalDate;

public interface MainService {

  LocalDate getProcessingDate(Main main);

}
