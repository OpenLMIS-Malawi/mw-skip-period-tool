package mw.gov.health.lmis.migration.tool.openlmis.referencedata.util;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import com.google.common.collect.Sets;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicLevel;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicZone;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderedDisplayValue;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingSchedule;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroup;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroupProgramSchedule;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.SupervisoryNode;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.scm.domain.AdjustmentType;
import mw.gov.health.lmis.migration.tool.scm.domain.Product;

import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@SuppressWarnings("PMD.TooManyMethods")
@Component
public class ReferenceDataCreator {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceDataCreator.class);

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Creates a new instance of OpenLMIS facility.
   */
  public Facility facility(String name, String code, FacilityType facilityType,
                           GeographicZone zone) {
    LOGGER.info("Create facility: {}", code);

    Facility facility = new Facility();
    facility.setId(UUID.randomUUID());
    facility.setName(name.trim());
    facility.setCode(code.trim());
    facility.setActive(true);
    facility.setEnabled(true);
    facility.setType(facilityType);
    facility.setGeographicZone(zone);

    return facility;
  }

  /**
   * Creates new orderable instance.
   */
  public Orderable orderable(Product product) {
    LOGGER.info("Create orderable: {}", product.getName());

    Orderable orderable = new Orderable();
    orderable.setId(UUID.randomUUID());
    orderable.setProductCode(new Code(product.getProductId().trim()));
    orderable.setFullProductName(product.getName().trim());

    return orderable;
  }

  /**
   * Creates new user.
   */
  public User user(String username, String firstName, String lastName) {
    LOGGER.info("Create user: {}", username);

    User user = new User();
    user.setUsername(username.trim());
    user.setFirstName(firstName.trim());
    user.setLastName(lastName.trim());
    user.setEmail(username + "@migrationtool.com");
    user.setVerified(true);
    user.setActive(true);
    user.setLoginRestricted(false);
    user.setAllowNotify(false);

    return user;
  }

  /**
   * Creates processing period.
   */
  public ProcessingPeriod processingPeriod(Date dateTime, ProcessingSchedule schedule) {
    LOGGER.info("Create processing period: {}", dateTime);

    LocalDate startDate = dateTime
        .toInstant()
        .atZone(TimeZone.getTimeZone(toolProperties.getParameters().getTimeZone()).toZoneId())
        .toLocalDate()
        .with(firstDayOfMonth());
    LocalDate endDate = startDate.with(lastDayOfMonth());

    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    processingPeriod.setId(UUID.randomUUID());
    processingPeriod.setName(startDate.getMonth() + "-" + endDate.getMonth());
    processingPeriod.setStartDate(startDate);
    processingPeriod.setEndDate(endDate);
    processingPeriod.setProcessingSchedule(schedule);

    return processingPeriod;
  }

  /**
   * Creates new program.
   */
  public Program program(String programCode) {
    LOGGER.info("Create program: {}", programCode);

    Program program = new Program();
    program.setName(programCode.trim());
    program.setCode(new Code(programCode.trim()));
    program.setPeriodsSkippable(true);

    return program;
  }

  /**
   * Creates new geographic level.
   */
  public GeographicLevel geographicLevel() {
    LOGGER.info("Create geographic level: zone");

    GeographicLevel level = new GeographicLevel();
    level.setCode("zone");
    level.setLevelNumber(2);
    level.setName("zone");

    return level;
  }

  /**
   * Creates new geographic zone.
   */
  public GeographicZone geographicZone(String code, GeographicLevel level) {
    LOGGER.info("Create geographic zone: {}", code);

    GeographicZone zone = new GeographicZone();
    zone.setName(code);
    zone.setCode(code);
    zone.setLevel(level);

    return zone;
  }

  /**
   * Creates new stock adjustment reason.
   */
  public StockAdjustmentReason stockAdjustmentReason(Program program, AdjustmentType type) {
    LOGGER.info("Create stock adjustment reason: {}", type.getCode());

    StockAdjustmentReason reason = new StockAdjustmentReason();
    reason.setId(UUID.randomUUID());
    reason.setProgram(program);
    reason.setName(type.getName().trim());
    reason.setDescription(type.getName().trim());
    reason.setAdditive(!type.getNegative());

    return reason;
  }

  /**
   * Creates new facility type.
   */
  public FacilityType facilityType() {
    LOGGER.info("Create facility type");

    FacilityType type = new FacilityType();
    type.setCode("first_facility_type");

    return type;
  }

  /**
   * Creates new orderable display category.
   */
  public OrderableDisplayCategory orderableDisplayCategory(
      mw.gov.health.lmis.migration.tool.scm.domain.Program program) {
    LOGGER.info("Create orderable display category: {}", program.getName());

    String displayName = program.getName().trim();
    Integer displayOrder = program.getOrder();

    OrderableDisplayCategory category = new OrderableDisplayCategory();
    category.setCode(new Code(displayName.replace(' ', '_')));
    category.setOrderedDisplayValue(new OrderedDisplayValue(displayName, displayOrder));

    return category;
  }

  /**
   * Creates new program orderable.
   */
  public ProgramOrderable programOrderable(Program program, Orderable product,
                                           OrderableDisplayCategory category,
                                           int displayOrder) {
    LOGGER.info("Create program orderable: {};{}", program.getName(), product.getFullProductName());

    ProgramOrderable programOrderable = new ProgramOrderable();
    programOrderable.setProgram(program);
    programOrderable.setProduct(product);
    programOrderable.setActive(true);
    programOrderable.setFullSupply(true);
    programOrderable.setOrderableDisplayCategory(category);
    programOrderable.setDisplayOrder(displayOrder);
    programOrderable.setPricePerPack(Money.of(CurrencyUnit.USD, (double) 5));

    return programOrderable;
  }

  /**
   * Creates new processing schedule.
   */
  public ProcessingSchedule processingSchedule() {
    LOGGER.info("Create processing schedule");

    ProcessingSchedule schedule = new ProcessingSchedule();
    schedule.setCode("processing-schedule-one");
    schedule.setName(schedule.getCode());

    return schedule;
  }

  /**
   * Creates new supervisory node.
   */
  public SupervisoryNode supervisoryNode(Facility facility) {
    LOGGER.info("Create supervisory node for facility: {}", facility.getCode());

    SupervisoryNode node = new SupervisoryNode();
    node.setCode("supervisory-node-" + facility.getCode());
    node.setFacility(facility);

    return node;
  }

  /**
   * Creates new requisition group.
   */
  public RequisitionGroup requisitionGroup(SupervisoryNode node, Iterable<Facility> facilities) {
    LOGGER.info("Create requisition group for node: {}", node.getCode());

    RequisitionGroup group = new RequisitionGroup();
    group.setCode("requisition-group-" + RandomStringUtils.random(5));
    group.setName(group.getCode());
    group.setSupervisoryNode(node);
    group.setMemberFacilities(Sets.newHashSet(facilities));

    return group;
  }

  /**
   * Creates new requisition group program schedule.
   */
  public RequisitionGroupProgramSchedule requisitionGroupProgramSchedule(RequisitionGroup group,
                                                                         Program program,
                                                                         ProcessingSchedule sche) {
    LOGGER.info("Create requisition group program schedule");

    RequisitionGroupProgramSchedule rgps = new RequisitionGroupProgramSchedule();
    rgps.setRequisitionGroup(group);
    rgps.setProgram(program);
    rgps.setProcessingSchedule(sche);

    return rgps;
  }

}
