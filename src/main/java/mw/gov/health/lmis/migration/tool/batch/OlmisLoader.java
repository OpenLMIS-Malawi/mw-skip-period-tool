package mw.gov.health.lmis.migration.tool.batch;

import static org.springframework.util.CollectionUtils.isEmpty;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.Pair;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.Order;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderNumberConfiguration;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.repository.OrderRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusChange;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusMessage;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;

import java.time.chrono.ChronoZonedDateTime;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OlmisLoader implements ItemWriter<List<Pair<Requisition, Order>>> {

  @Autowired
  private OlmisFacilityRepository olmisFacilityRepository;

  @Autowired
  private OlmisProcessingPeriodRepository olmisProcessingPeriodRepository;

  @Autowired
  private OlmisOrderableRepository olmisOrderableRepository;

  @Autowired
  private OlmisRequisitionRepository olmisRequisitionRepository;

  @Autowired
  private OlmisUserRepository olmisUserRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OlmisProgramRepository olmisProgramRepository;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Writes Requisitions into OpenLMIS database.
   */
  @Override
  public void write(List<? extends List<Pair<Requisition, Order>>> items) throws Exception {
    items
        .stream()
        .flatMap(Collection::stream)
        .forEach(pair -> {
          Requisition requisition = pair.getLeft();
          olmisRequisitionRepository.save(requisition);

          OrderNumberConfiguration config = toolProperties
              .getParameters()
              .getOrderNumberConfiguration();
          Program program = olmisProgramRepository.findOne(requisition.getProgramId());
          Order order = pair.getRight();

          order.setOrderCode(config.generateOrderNumber(order, program));
          
          orderRepository.save(order);

          print(requisition);
        });
  }

  private void print(Requisition requisition) {
    Facility facility = olmisFacilityRepository.findOne(requisition.getFacilityId());
    ProcessingPeriod period = olmisProcessingPeriodRepository
        .findOne(requisition.getProcessingPeriodId());

    String format = "%-8s|%-57s|%-14s|%-18s|%-18s|%-17s|%-21s|%-18s|%-20s|%-17s|%-9s%n";

    System.err.printf("Facility (code): %s (%s)%n", facility.getName(), facility.getCode());
    System.err.printf("Period: %s%n", printPeriod(period));

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
        .filter(change -> change.getStatus() == ExternalStatus.INITIATED)
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

    List<StatusMessage> statusMessages = requisition
        .getStatusMessages();
    String comment = isEmpty(statusMessages) ? null : statusMessages
        .stream()
        .map(StatusMessage::getBody)
        .collect(Collectors.joining("; "));

    System.err.printf("Comment: %s%n", Optional.ofNullable(comment).orElse(""));
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
