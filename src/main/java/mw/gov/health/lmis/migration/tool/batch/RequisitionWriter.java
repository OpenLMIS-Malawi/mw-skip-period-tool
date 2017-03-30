package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionStatus;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusChange;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.repository.FacilityRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ItemRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.MainRepository;

import java.time.LocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class RequisitionWriter implements ItemWriter<List<Requisition>> {

  @Autowired
  private MainRepository mainRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private OlmisFacilityRepository olmisFacilityRepository;

  @Autowired
  private OlmisProcessingPeriodRepository olmisProcessingPeriodRepository;

  @Autowired
  private OlmisOrderableRepository olmisOrderableRepository;

  @Autowired
  private OlmisRequisitionRepository olmisRequisitionRepository;

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private OlmisUserRepository olmisUserRepository;

  /**
   * Writes Requisitions into OpenLMIS database.
   */
  @Override
  public void write(List<? extends List<Requisition>> items) throws Exception {
    items
        .stream()
        .flatMap(Collection::stream)
        .forEach(requisition -> {
          olmisRequisitionRepository.save(requisition);
          print(requisition);
        });
  }

  private void print(Requisition requisition) {
    Facility olmisFacility = olmisFacilityRepository.findOne(requisition.getFacilityId());
    ProcessingPeriod period = olmisProcessingPeriodRepository
        .findOne(requisition.getProcessingPeriodId());

    String format =
        "%-8s|%-57s|%-14s|%-18s|%-18s|%-17s|%-21s|%-18s|%-20s|%-17s|%-9s%n";

    System.err.printf(
        "Facility (code): %s (%s)%n", olmisFacility.getName(), olmisFacility.getCode()
    );
    System.err.printf("Period: %s%n", printPeriod(period));

    mw.gov.health.lmis.migration.tool.scm.domain.Facility scmFacility = facilityRepository
        .findByCode(olmisFacility.getCode());
    LocalDateTime processingDate = period.getStartDate().atStartOfDay();

    Main main = mainRepository.findOne(new Main.ComplexId(scmFacility, processingDate));

    System.err.printf(
        format,
        "Product", "Product Description", "Stock on Hand",
        "Adjustment Amount", "Quantity Consumed",
        "Stocked Out Days", "Adjusted Consumption", "Months of Stock", "Calculated Quantity",
        "Reorder Quantity", "Receipts"
    );
    for (RequisitionLineItem line : requisition.getRequisitionLineItems()) {
      Orderable orderable = olmisOrderableRepository.findOne(line.getOrderableId());

      System.err.printf(
          format,
          orderable.getProductCode(),
          orderable.getName(),
          line.getStockOnHand(),
          line.getTotalLossesAndAdjustments(),
          line.getTotalConsumedQuantity(),
          line.getTotalStockoutDays(),
          line.getAdjustedConsumption(),
          line.getMaxPeriodsOfStock(),
          line.getCalculatedOrderQuantity(),
          line.getRequestedQuantity(),
          line.getTotalReceivedQuantity()
      );
    }

    StatusChange statusChange = requisition
        .getStatusChanges()
        .stream()
        .filter(change -> change.getStatus() == RequisitionStatus.INITIATED)
        .findFirst()
        .orElse(null);
    User user = olmisUserRepository.findOne(statusChange.getAuthorId());

    System.err.println();
    System.err.printf(
        "First input (date):  %-10s (%-10s)%n",
        user.getUsername(), printDate(requisition.getCreatedDate())
    );
    System.err.printf(
        "Last changed (date): %-10s (%-10s)%n",
        user.getUsername(), printDate(requisition.getModifiedDate())
    );
    // TODO: how to handle general comment and notes for each product/column
    System.err.printf(
        "Comment: %s%n",
        Optional.ofNullable(requisition.getDraftStatusMessage()).orElse("")
    );
  }

  private String printDate(ChronoZonedDateTime dateTime) {
    return null == dateTime ? "" : dateTime.toLocalDate().toString();
  }

  private String printPeriod(ProcessingPeriod period) {
    return String.format(
        "%s-%s %d",
        period.getStartDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
        period.getEndDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
        period.getStartDate().getYear()
    );
  }

}
