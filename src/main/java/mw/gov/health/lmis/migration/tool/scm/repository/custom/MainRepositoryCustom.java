package mw.gov.health.lmis.migration.tool.scm.repository.custom;

import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.util.List;

public interface MainRepositoryCustom {

  List<Main> searchInPeriod(Integer period, Integer page, Integer pageSize);

}
