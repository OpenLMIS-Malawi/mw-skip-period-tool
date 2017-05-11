package mw.gov.health.lmis.migration.tool.batch;


import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.service.RequisitionService;

import java.util.List;

@Component
public class RequisitionProcessor implements ItemProcessor<Requisition, Requisition> {

  @Autowired
  private RequisitionService requisitionService;

  @Override
  public Requisition process(Requisition requisition) throws Exception {
    RequisitionTemplate template = requisition.getTemplate();

    int numberOfPreviousPeriodsToAverage;
    List<Requisition> previousRequisitions;
    if (template.getNumberOfPeriodsToAverage() == null) {
      numberOfPreviousPeriodsToAverage = 0;
      previousRequisitions = requisitionService.getRecentRequisitions(requisition, 1);
    } else {
      numberOfPreviousPeriodsToAverage = template.getNumberOfPeriodsToAverage() - 1;
      previousRequisitions = requisitionService
          .getRecentRequisitions(requisition, numberOfPreviousPeriodsToAverage);
    }

    if (numberOfPreviousPeriodsToAverage > previousRequisitions.size()) {
      numberOfPreviousPeriodsToAverage = previousRequisitions.size();
    }

    requisition.setPreviousRequisitions(previousRequisitions);
    requisition.setPreviousAdjustedConsumptions(numberOfPreviousPeriodsToAverage);

    return requisition;
  }
  
}
