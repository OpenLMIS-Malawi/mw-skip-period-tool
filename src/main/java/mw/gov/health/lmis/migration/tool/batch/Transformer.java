package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.LineItemFieldsCalculator.calculateTotalLossesAndAdjustments;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.Pair;
import mw.gov.health.lmis.migration.tool.config.ToolProgramMapping;
import mw.gov.health.lmis.migration.tool.config.ToolProgramWarehouseMapping;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus;
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
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusMessage;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StockAdjustment;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.AdjustmentType;
import mw.gov.health.lmis.migration.tool.scm.domain.CategoryProductJoin;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.domain.Product;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentTypeRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.CategoryProductJoinRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.CommentRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ItemRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ProductRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ProgramRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class Transformer implements ItemProcessor<Main, List<Pair<Requisition, Order>>> {
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

  @Autowired
  private ToolProperties toolProperties;

  @Autowired
  private AdjustmentRepository adjustmentRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CategoryProductJoinRepository categoryProductJoinRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private AdjustmentTypeRepository adjustmentTypeRepository;

  /**
   * Converts the given {@link Main} object into {@link Requisition} object.
   */
  @Override
  public List<Pair<Requisition, Order>> process(Main main) {
    List<Item> items = itemRepository.search(main.getProcessingDate(), main.getFacility());

    Multimap<String, Item> groups = HashMultimap.create();
    for (Item item : items) {
      toolProperties
          .getMapping()
          .getPrograms()
          .stream()
          .filter(cp -> null != cp
              .getCategories()
              .stream()
              .filter(cat -> {
                CategoryProductJoin join = categoryProductJoinRepository
                    .findById(item.getCategoryProduct());
                mw.gov.health.lmis.migration.tool.scm.domain.Program program =
                    programRepository.findByProgramId(join.getProgram());
                return equalsIgnoreCase(cat, program.getName());
              })
              .findFirst()
              .orElse(null)
          )
          .map(ToolProgramMapping::getCode)
          .forEach(code -> groups.put(code, item));
    }

    List<Pair<Requisition, Order>> pairs = Lists.newArrayList();
    for (Map.Entry<String, Collection<Item>> entry : groups.asMap().entrySet()) {
      pairs.add(createRequisition(entry.getKey(), entry.getValue(), main));
    }

    return pairs;
  }

  private Pair<Requisition, Order> createRequisition(String programCode, Collection<Item> items,
                                                     Main main) {
    String code = toolProperties
        .getMapping()
        .getFacilities()
        .getProperty(main.getFacility(), main.getFacility());
    mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility facility =
        olmisFacilityRepository.findByCode(code);

    Program program = olmisProgramRepository.findByName(programCode);

    Requisition requisition = new Requisition();
    requisition.setFacilityId(facility.getId());
    requisition.setProgramId(program.getId());
    requisition.setEmergency(false);
    requisition.setStatus(ExternalStatus.INITIATED);

    ProcessingPeriod period = olmisProcessingPeriodRepository
        .findByStartDate(safeNull(main.getProcessingDate()).toLocalDate());

    requisition.setProcessingPeriodId(period.getId());
    requisition.setNumberOfMonthsInPeriod(period.getDurationInMonths());

    List<Pair<Orderable, Double>> pairs = items
        .stream()
        .map(item -> {
          Product product = productRepository.findById(item.getProduct());
          String defaultName = product.getName().trim();
          String name = toolProperties
              .getMapping()
              .getProducts()
              .getProperty(defaultName, defaultName);

          return new Pair<>(
              olmisOrderableRepository.findFirstByName(name),
              getMonthsOfStock(item));
        })
        .collect(Collectors.toList());

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

    for (RequisitionLineItem line : requisition.getRequisitionLineItems()) {
      updateLine(line, requisition, items);
    }

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
        .filter(elem -> {
          Product product = productRepository.findById(elem.getProduct());
          String defaultName = product.getName().trim();
          String name = toolProperties
              .getMapping()
              .getProducts()
              .getProperty(defaultName, defaultName);
          return name.equals(orderable.getName());
        })
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("can't find correct item element from list"));

    RequisitionLineItem requisitionLineItem = new RequisitionLineItem();
    requisitionLineItem.setSkipped(false);

    requisitionLineItem.setTotalReceivedQuantity(item.getReceipts());
    requisitionLineItem.setTotalConsumedQuantity(item.getDispensedQuantity());

    Program program = olmisProgramRepository.findOne(requisition.getProgramId());
    List<StockAdjustment> stockAdjustments = Lists.newArrayList();
    for (Adjustment adjustment : adjustmentRepository.search(item.getId())) {
      if (null == adjustment.getQuantity()) {
        continue;
      }

      AdjustmentType type = adjustmentTypeRepository.findByType(adjustment.getType());
      String defaultName = type.getName();
      String name = toolProperties
          .getMapping()
          .getStockAdjustmentReasons()
          .getProperty(defaultName, defaultName);

      StockAdjustmentReason stockAdjustmentReason = olmisStockAdjustmentReasonRepository
          .findByProgramAndName(program, name);

      StockAdjustment stockAdjustment = new StockAdjustment();
      stockAdjustment.setReasonId(stockAdjustmentReason.getId());
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
    requisitionLineItem.setRequestedQuantityExplanation(
        toolProperties.getParameters().getRequestedQuantityExplanation()
    );
    requisitionLineItem.setAdjustedConsumption(item.getAdjustedDispensedQuantity());
    requisitionLineItem.setNonFullSupply(false);

    line.updateFrom(requisitionLineItem);
  }

  private ZonedDateTime safeNull(Date date) {
    if (null == date) {
      return null;
    }

    String timeZone = toolProperties.getParameters().getTimeZone();
    return date
        .toInstant()
        .atZone(TimeZone.getTimeZone(timeZone).toZoneId());
  }

  private Order convertToOrder(Requisition requisition, User user, Program program,
                               Facility facility) {
    Facility warehouse = null;
    List<ToolProgramWarehouseMapping> warehouses = toolProperties
        .getMapping()
        .getPrograms()
        .stream()
        .filter(mp -> mp.getCode().equals(program.getCode().toString()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException(
                "Can't find warehouse mapping for program: " + program.getCode()
            )
        )
        .getWarehouses();

    GeographicZone zone = facility.getGeographicZone();

    while (null != zone) {
      String zoneName = zone.getName();

      for (ToolProgramWarehouseMapping supplying : warehouses) {
        if (null == supplying.getGeographicZone()
            || containsIgnoreCase(zoneName, supplying.getGeographicZone())) {
          warehouse = olmisFacilityRepository.findByCode(supplying.getCode());
          break;
        }
      }

      if (null == warehouse) {
        zone = zone.getParent();
      } else {
        break;
      }
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

    for (Item item : items) {
      notes.add(item.getNote());

      commentRepository.search(item.getId())
          .forEach(comment -> notes.add(
              comment.getType() + ": " + comment.getComment()
          ));
    }

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
