package mw.gov.health.lmis.migration.tool.openlmis.referencedata.util;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderedDisplayValue;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.TradeItem;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.scm.domain.AdjustmentType;
import mw.gov.health.lmis.migration.tool.scm.domain.Product;
import mw.gov.health.lmis.migration.tool.scm.domain.SystemDefault;
import mw.gov.health.lmis.migration.tool.scm.repository.SystemDefaultRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ReferenceDataUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceDataUtil.class);

  @Autowired
  private SystemDefaultRepository systemDefaultRepository;

  /**
   * Creates a new instance of OpenLMIS facility.
   */
  public Facility create(String name, String code, FacilityType facilityType) {
    LOGGER.info("Create facility: {}", code);

    Facility facility = new Facility();
    facility.setId(UUID.randomUUID());
    facility.setName(name);
    facility.setCode(code);
    facility.setActive(true);
    facility.setEnabled(true);
    facility.setType(facilityType);

    return facility;
  }

  /**
   * Creates new orderable instance.
   */
  public Orderable create(Product product) {
    LOGGER.info("Create orderable: {}", product.getName());

    Orderable orderable = new TradeItem();
    orderable.setId(UUID.randomUUID());
    orderable.setProductCode(new Code(product.getProductId()));
    orderable.setName(product.getName());

    return orderable;
  }

  /**
   * Creates new user.
   */
  public User create(String username, String firstName, String lastName) {
    User user = new User();
    user.setUsername(username);
    user.setFirstName(firstName);
    user.setLastName(lastName);
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
  public ProcessingPeriod create(LocalDateTime dateTime) {
    LOGGER.info("Create processing period: {}", dateTime);

    SystemDefault systemDefault = systemDefaultRepository
        .findAll()
        .iterator()
        .next();

    long numberOfMonths = systemDefault.getReportingPeriod() - 1L;

    LocalDate startDate = dateTime.toLocalDate().with(firstDayOfMonth());
    LocalDate endDate = startDate.plusMonths(numberOfMonths).with(lastDayOfMonth());

    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    processingPeriod.setId(UUID.randomUUID());
    processingPeriod.setName(startDate.getMonth() + "-" + endDate.getMonth());
    processingPeriod.setStartDate(startDate);
    processingPeriod.setEndDate(endDate);

    return processingPeriod;
  }

  /**
   * Creates new program.
   */
  public Program create(String programCode) {
    LOGGER.info("Create program: {}", programCode);

    Program program = new Program();
    program.setName(programCode);
    program.setCode(new Code(programCode));
    program.setPeriodsSkippable(true);

    return program;
  }

  /**
   * Creates new stock adjustment reason.
   */
  public StockAdjustmentReason create(Program program, AdjustmentType adjustmentType) {
    LOGGER.info("Create stock adjustment reason: {}", adjustmentType.getCode());

    StockAdjustmentReason reason = new StockAdjustmentReason();
    reason.setId(UUID.randomUUID());
    reason.setProgram(program);
    reason.setName(adjustmentType.getName());
    reason.setDescription(adjustmentType.getName());
    reason.setAdditive(!adjustmentType.getNegative());

    return reason;
  }

  /**
   * Creates new facility type.
   */
  public FacilityType create() {
    LOGGER.info("Create facility type");

    FacilityType type = new FacilityType();
    type.setCode("first_facility_type");

    return type;
  }

  /**
   * Creates new orderable display category.
   */
  public OrderableDisplayCategory create(mw.gov.health.lmis.migration.tool.scm.domain.Program pro) {
    LOGGER.info("Create orderable display category: {}", pro.getName());

    String displayName = pro.getName();
    Integer displayOrder = pro.getOrder();

    OrderableDisplayCategory category = new OrderableDisplayCategory();
    category.setCode(new Code(displayName.replace(' ', '_')));
    category.setOrderedDisplayValue(new OrderedDisplayValue(displayName, displayOrder));

    return category;
  }

  /**
   * Creates new program orderable.
   */
  public ProgramOrderable create(Program program, Orderable product,
                                 OrderableDisplayCategory category,
                                 int displayOrder, double pricePerPack) {
    LOGGER.info("Create program orderable: {};{}", program.getName(), product.getName());

    ProgramOrderable programOrderable = new ProgramOrderable();
    programOrderable.setProgram(program);
    programOrderable.setProduct(product);
    programOrderable.setActive(true);
    programOrderable.setFullSupply(true);
    programOrderable.setOrderableDisplayCategory(category);
    programOrderable.setDisplayOrder(displayOrder);
    programOrderable.setPricePerPack(Money.of(CurrencyUnit.USD, pricePerPack));

    return programOrderable;
  }

}
