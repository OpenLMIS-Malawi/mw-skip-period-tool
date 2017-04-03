package mw.gov.health.lmis.migration.tool.openlmis.referencedata.service;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;

import java.util.List;
import java.util.UUID;

public interface ProcessingPeriodService {

  List<ProcessingPeriod> findPreviousPeriods(UUID periodId, int amount);
  
}
