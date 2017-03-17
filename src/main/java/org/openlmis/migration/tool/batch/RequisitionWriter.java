package org.openlmis.migration.tool.batch;

import org.openlmis.migration.tool.domain.Comment;
import org.openlmis.migration.tool.domain.Item;
import org.openlmis.migration.tool.domain.Main;
import org.openlmis.migration.tool.domain.Purpose;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Facility;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OpenLmisFacilityRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OpenLmisOrderableRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OpenLmisProcessingPeriodRepository;
import org.openlmis.migration.tool.openlmis.requisition.domain.Requisition;
import org.openlmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import org.openlmis.migration.tool.repository.ItemRepository;
import org.openlmis.migration.tool.repository.MainRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RequisitionWriter implements ItemWriter<Requisition> {

  @Autowired
  private MainRepository mainRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private OpenLmisFacilityRepository openLmisFacilityRepository;

  @Autowired
  private OpenLmisProcessingPeriodRepository openLmisProcessingPeriodRepository;

  @Autowired
  private OpenLmisOrderableRepository openLmisOrderableRepository;

  /**
   * Writes Reuisitons into OpenLMIS database.
   */
  @Override
  public void write(List<? extends Requisition> requisitions) {
    requisitions.forEach(this::print);
  }

  private void print(Requisition requisition) {
    Facility facility = openLmisFacilityRepository.findOne(requisition.getFacilityId());
    ProcessingPeriod period = openLmisProcessingPeriodRepository
        .findOne(requisition.getProcessingPeriodId());

    String format =
        "%-8s|%-57s|%-14s|%-18s|%-16s|%-18s|%-17s|%-17s|%-21s|%-18s|%-20s|%-17s|%-9s|%-13s|%-5s%n";

    System.err.printf("Facility (code): %s (%s)%n", facility.getName(), facility.getCode());
    System.err.printf("Period: %s%n", printPeriod(period));

    if (!requisition.getRequisitionLineItems().isEmpty()) {
      RequisitionLineItem first = requisition.getRequisitionLineItems().get(0);
      Item item = itemRepository.findOne(Integer.valueOf(first.getRemarks()));
      Main main = mainRepository
          .findOne(new Main.ComplexId(item.getFacility(), item.getProcessingDate()));

      System.err.printf(
          "Date Received: %s Date Shipment Received: %s%n%n",
          printDate(main.getReceivedDate()), printDate(main.getShipmentReceivedData())
      );
      System.err.printf(
          format,
          "Product", "Product Description", "Stock on Hand",
          "Adjustment Amount", "Adjustment Type", "Quantity Consumed", "Purpose of Use",
          "Stocked Out Days", "Adjusted Consumption", "Months of Stock", "Calculated Quantity",
          "Reorder Quantity", "Receipts", "Stocked Out?", "Notes"
      );

      for (RequisitionLineItem line : requisition.getRequisitionLineItems()) {
        item = itemRepository.findOne(Integer.valueOf(line.getRemarks()));
        Orderable orderableDto = openLmisOrderableRepository.findByName(item);

        System.err.printf(
            format,
            orderableDto.getProductCode(),
            orderableDto.getName(),
            line.getStockOnHand(),
            line.getTotalLossesAndAdjustments(),
            item.getAdjustmentType(),
            line.getTotalConsumedQuantity(),
            countPurposes(item.getPurposes()),
            line.getTotalStockoutDays(),
            line.getAdjustedConsumption(),
            getMonthsOfStock(line),
            line.getCalculatedOrderQuantity(),
            line.getRequestedQuantity(),
            line.getTotalReceivedQuantity(),
            item.getProductStockedOut(),
            printNotes(item.getNotes())
        );
      }

      System.err.println();
      System.err.printf(
          "First input (date):  %-10s (%-10s)%n",
          main.getCreatedBy(), printDate(requisition.getCreatedDate())
      );
      System.err.printf(
          "Last changed (date): %-10s (%-10s)%n",
          main.getModifiedBy(), printDate(requisition.getModifiedDate())
      );
      System.err.printf(
          "Comment: %s%n",
          Optional.ofNullable(requisition.getDraftStatusMessage()).orElse("")
      );
    }
  }

  private int countPurposes(List<Purpose> purposes) {
    return null != purposes
        ? purposes.stream().map(Purpose::getQuantity).reduce(0, (left, right) -> left + right)
        : 0;
  }

  private String printNotes(List<Comment> comments) {
    if (null == comments || comments.isEmpty()) {
      return "";
    }

    return comments
        .stream()
        .map(note -> note.getType().getName() + ": " + note.getComment())
        .collect(Collectors.joining(", "));
  }

  private String printDate(ChronoLocalDateTime dateTime) {
    return null == dateTime ? "" : dateTime.toLocalDate().toString();
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

  private Double getMonthsOfStock(RequisitionLineItem requisitionLineItem) {
    if (0 == requisitionLineItem.getAdjustedConsumption()) {
      return 0.0;
    }

    return BigDecimal.valueOf(requisitionLineItem.getStockOnHand())
        .divide(
            BigDecimal.valueOf(requisitionLineItem.getAdjustedConsumption()),
            1,
            BigDecimal.ROUND_HALF_UP
        )
        .doubleValue();
  }

}
