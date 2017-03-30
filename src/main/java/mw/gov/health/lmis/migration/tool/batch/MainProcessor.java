package mw.gov.health.lmis.migration.tool.batch;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.LineItemFieldsCalculator.calculateTotalLossesAndAdjustments;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.Pair;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.Order;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderStatus;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicZone;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusMessage;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StockAdjustment;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.repository.ItemRepository;
import mw.gov.health.lmis.migration.tool.scm.util.Grouping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MainProcessor implements ItemProcessor<Main, List<Pair<Requisition, Order>>> {
  private static final String USERNAME = "supply chain manager";

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private OlmisFacilityRepository olmisFacilityRepository;

  @Autowired
  private OlmisProgramRepository olmisProgramRepository;

  @Autowired
  private OlmisProcessingPeriodRepository olmisProcessingPeriodRepository;

  @Autowired
  private OlmisRequisitionTemplateRepository olmisRequisitionTemplateRepository;

  @Autowired
  private OlmisStockAdjustmentReasonRepository olmisStockAdjustmentReasonRepository;

  @Autowired
  private OlmisOrderableRepository olmisOrderableRepository;

  @Autowired
  private OlmisRequisitionRepository olmisRequisitionRepository;

  @Autowired
  private OlmisUserRepository olmisUserRepository;

  /**
   * Converts the given {@link Main} object into {@link Requisition} object.
   */
  @Override
  public List<Pair<Requisition, Order>> process(Main main) {
    List<Item> items = itemRepository.findByProcessingDateAndFacility(
        main.getId().getProcessingDate(), main.getId().getFacility()
    );

    return Grouping
        .groupByCategoryName(items, item -> item.getCategoryProduct().getProgram().getName())
        .asMap()
        .entrySet()
        .stream()
        .map(entry -> createRequisition(entry.getKey(), entry.getValue(), main))
        .collect(Collectors.toList());
  }

  private Pair<Requisition, Order> createRequisition(String programCode, Collection<Item> items,
                                                     Main main) {

    mw.gov.health.lmis.migration.tool.scm.domain.Facility mainFacility = main.getId().getFacility();
    mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility facility =
        olmisFacilityRepository.findByCode(mainFacility.getCode());

    Program program = olmisProgramRepository.findByName(programCode);

    Requisition requisition = new Requisition();
    requisition.setFacilityId(facility.getId());
    requisition.setProgramId(program.getId());
    requisition.setEmergency(false);
    requisition.setStatus(ExternalStatus.INITIATED);

    ProcessingPeriod period = olmisProcessingPeriodRepository
        .findByStartDate(main.getId().getProcessingDate().toLocalDate().with(firstDayOfMonth()));

    requisition.setProcessingPeriodId(period.getId());
    requisition.setNumberOfMonthsInPeriod(period.getDurationInMonths());

    List<Pair<Orderable, Double>> pairs = items
        .stream()
        .map(item -> new Pair<>(
            olmisOrderableRepository.findFirstByName(item.getProduct().getName()),
            getMonthsOfStock(item))
        ).collect(Collectors.toList());

    RequisitionTemplate template = olmisRequisitionTemplateRepository
        .findByProgramId(program.getId());

    int numberOfPreviousPeriodsToAverage;
    List<Requisition> previousRequisitions;
    if (template.getNumberOfPeriodsToAverage() == null) {
      numberOfPreviousPeriodsToAverage = 0;
      previousRequisitions = getRecentRequisitions(requisition, 1);
    } else {
      numberOfPreviousPeriodsToAverage = template.getNumberOfPeriodsToAverage() - 1;
      previousRequisitions =
          getRecentRequisitions(requisition, numberOfPreviousPeriodsToAverage);
    }

    if (numberOfPreviousPeriodsToAverage > previousRequisitions.size()) {
      numberOfPreviousPeriodsToAverage = previousRequisitions.size();
    }

    User user = olmisUserRepository.findByUsername(USERNAME);

    requisition.initiate(template, pairs, previousRequisitions,
        numberOfPreviousPeriodsToAverage, null, user.getId());

    requisition.setAvailableNonFullSupplyProducts(Sets.newHashSet());

    requisition.setCreatedDate(safeNull(main.getCreatedDate()));
    requisition.setModifiedDate(safeNull(main.getModifiedDate()));

    requisition
        .getRequisitionLineItems()
        .forEach(line -> updateLine(line, requisition, items));

    List<Orderable> products = Lists.newArrayList(olmisOrderableRepository.findAll());

    requisition.submit(products, user.getId());
    requisition.authorize(products, user.getId());
    addStatusMessage(requisition, main, items, user);

    requisition.approve(null, products, user.getId());

    Order order = convertToOrder(requisition, user, program, facility);

    return new Pair<>(requisition, order);
  }

  private void updateLine(RequisitionLineItem line, Requisition requisition,
                          Collection<Item> items) {
    Orderable orderable = olmisOrderableRepository.findOne(line.getOrderableId());
    Item item = items
        .stream()
        .filter(elem -> elem.getProduct().getName().equals(orderable.getName()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("can't find correct item element from list"));

    RequisitionLineItem requisitionLineItem = new RequisitionLineItem();
    requisitionLineItem.setSkipped(false);

    requisitionLineItem.setTotalReceivedQuantity(item.getReceipts());
    requisitionLineItem.setTotalConsumedQuantity(item.getDispensedQuantity());

    Program program = olmisProgramRepository.findOne(requisition.getProgramId());
    List<StockAdjustment> stockAdjustments = Lists.newArrayList();
    for (Adjustment adjustment : item.getAdjustments()) {
      if (null == adjustment.getQuantity()) {
        continue;
      }

      String name = adjustment.getType().getName();

      if ("de credit".equalsIgnoreCase(name)) {
        name = "transfer in";
      } else if ("de debit".equalsIgnoreCase(name)) {
        name = "transfer out";
      }

      StockAdjustmentReason stockAdjustmentReasonDto = olmisStockAdjustmentReasonRepository
          .findByProgramAndName(program, name);

      StockAdjustment stockAdjustment = new StockAdjustment();
      stockAdjustment.setReasonId(stockAdjustmentReasonDto.getId());
      stockAdjustment.setQuantity(adjustment.getQuantity());

      stockAdjustments.add(stockAdjustment);
    }

    requisitionLineItem.setStockAdjustments(stockAdjustments);
    requisitionLineItem.setTotalLossesAndAdjustments(
        calculateTotalLossesAndAdjustments(
            requisitionLineItem,
            Lists.newArrayList(olmisStockAdjustmentReasonRepository.findAll())
        )
    );
    requisitionLineItem.setTotalStockoutDays(item.getStockedOutDays().intValue());
    requisitionLineItem.setStockOnHand(item.getClosingBalance());
    requisitionLineItem.setCalculatedOrderQuantity(item.getCalculatedRequiredQuantity());
    requisitionLineItem.setRequestedQuantity(item.getRequiredQuantity());
    requisitionLineItem.setRequestedQuantityExplanation("transferred from supply manager");
    requisitionLineItem.setAdjustedConsumption(item.getAdjustedDispensedQuantity());
    requisitionLineItem.setNonFullSupply(false);

    line.updateFrom(requisitionLineItem);
  }

  private ZonedDateTime safeNull(LocalDateTime dateTime) {
    return null == dateTime
        ? null
        : dateTime.atZone(TimeZone.getTimeZone("CAT").toZoneId());
  }

  private Order convertToOrder(Requisition requisition, User user, Program program,
                               Facility facility) {
    Facility warehouse = null;

    if ("em".equalsIgnoreCase(program.getCode().toString())) {
      GeographicZone zone = facility.getGeographicZone();

      while (null != zone) {
        String zoneName = zone.getName();

        if (startsWithIgnoreCase(zoneName, "central")) {
          warehouse = olmisFacilityRepository.findByName("CMST - Central");
        } else if (startsWithIgnoreCase(zoneName, "south")) {
          warehouse = olmisFacilityRepository.findByName("CMST - South");
        } else if (startsWithIgnoreCase(zoneName, "northern")) {
          warehouse = olmisFacilityRepository.findByName("CMST - North");
        }

        if (null == warehouse) {
          zone = zone.getParent();
        } else {
          break;
        }
      }
    } else {
      warehouse = olmisFacilityRepository.findByName("Program");
    }

    if (null != warehouse) {
      requisition.setSupplyingFacilityId(warehouse.getId());
    } else {
      throw new IllegalStateException(
          "can't find supplying facility for program: "
              + program.getName()
              + " and facility: "
              + facility.getName()
      );
    }

    requisition.release(user.getId());

    Order order = Order.newOrder(requisition, user);
    order.setStatus(OrderStatus.RECEIVED);

    return order;
  }

  private List<Requisition> getRecentRequisitions(Requisition requisition, int amount) {
    List<ProcessingPeriod> previousPeriods =
        findPreviousPeriods(requisition.getProcessingPeriodId(), amount);

    List<Requisition> recentRequisitions = new ArrayList<>();
    for (ProcessingPeriod period : previousPeriods) {
      List<Requisition> requisitionsByPeriod = getRequisitionsByPeriod(requisition, period);
      if (!requisitionsByPeriod.isEmpty()) {
        Requisition requisitionByPeriod = requisitionsByPeriod.get(0);
        recentRequisitions.add(requisitionByPeriod);
      }
    }
    return recentRequisitions;
  }

  private List<ProcessingPeriod> findPreviousPeriods(UUID periodId, int amount) {
    ProcessingPeriod period = olmisProcessingPeriodRepository.findOne(periodId);

    if (null == period) {
      return Collections.emptyList();
    }

    Collection<ProcessingPeriod> collection = olmisProcessingPeriodRepository
        .findByProcessingScheduleAndStartDate(
            period.getProcessingSchedule(),
            period.getStartDate()
        );

    if (null == collection || collection.isEmpty()) {
      return Collections.emptyList();
    }

    // create a list...
    List<ProcessingPeriod> list = new ArrayList<>(collection);
    // ...remove the latest period from the list...
    list.removeIf(p -> p.getId().equals(periodId));
    // .. and sort elements by startDate property DESC.
    list.sort(Comparator.comparing(ProcessingPeriod::getStartDate).reversed());

    if (amount > list.size()) {
      return list;
    }

    return list.subList(0, amount);
  }

  private List<Requisition> getRequisitionsByPeriod(Requisition requisition,
                                                    ProcessingPeriod period) {
    return olmisRequisitionRepository.findByFacilityIdAndProgramIdAndProcessingPeriodId(
        requisition.getFacilityId(), requisition.getProgramId(), period.getId()
    );
  }

  private void addStatusMessage(Requisition requisition, Main main,
                                Collection<Item> items, User user) {
    List<String> notes = Lists.newArrayList();
    notes.add(main.getNotes());

    items
        .forEach(item -> {
          notes.add(item.getNote());

          item
              .getNotes()
              .forEach(comment -> notes.add(
                  comment.getType().getName() + ": " + comment.getComment()
              ));
        });

    notes.removeIf(StringUtils::isBlank);

    String message = notes.stream().collect(Collectors.joining("; "));

    if (isNotBlank(message)) {
      requisition.setStatusMessages(Lists.newArrayList(StatusMessage.newStatusMessage(
          requisition, user.getId(), user.getFirstName(), user.getLastName(), message
      )));
    }
  }

  private Double getMonthsOfStock(Item item) {
    if (0 == item.getClosingBalance() || 0 == item.getAdjustedDispensedQuantity()) {
      return BigDecimal.ZERO.doubleValue();
    }

    return BigDecimal.valueOf(item.getClosingBalance())
        .divide(
            BigDecimal.valueOf(item.getAdjustedDispensedQuantity()),
            1,
            BigDecimal.ROUND_HALF_UP
        )
        .doubleValue();
  }

}
