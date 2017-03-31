package mw.gov.health.lmis.migration.tool.scm.repository.custom;

import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.time.Period;
import java.util.List;

public interface MainRepositoryCustom {

  List<Main> searchInPeriod(Period period, Integer page, Integer pageSize);

}
