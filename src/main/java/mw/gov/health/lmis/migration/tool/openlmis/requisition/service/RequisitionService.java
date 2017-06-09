package mw.gov.health.lmis.migration.tool.openlmis.requisition.service;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;

public interface RequisitionService {

  void addStatusMessage(Requisition requisition, User user, String generalNote);

  void convertToOrder(Requisition requisition, User user, Program program, Facility facility);
}
