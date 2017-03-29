package mw.gov.health.lmis.migration.tool.openlmis.requisition.util;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.ADJUSTED_CONSUMPTION;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.APPROVED_QUANTITY;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.AVERAGE_CONSUMPTION;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.BEGINNING_BALANCE;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.CALCULATED_ORDER_QUANTITY;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.DISPENSING_UNIT;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.MAXIMUM_STOCK_QUANTITY;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.NUMBER_OF_NEW_PATIENTS_ADDED;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.PACKS_TO_SHIP;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.PRICE_PER_PACK;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.PRODUCT;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.PRODUCT_CODE;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.REMARKS_COLUMN;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.REQUESTED_QUANTITY;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.REQUESTED_QUANTITY_EXPLANATION;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.SKIPPED_COLUMN;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.STOCK_ON_HAND;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.TOTAL_COLUMN;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.TOTAL_CONSUMED_QUANTITY;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.TOTAL_COST;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.TOTAL_LOSSES_AND_ADJUSTMENTS;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.TOTAL_RECEIVED_QUANTITY;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem.TOTAL_STOCKOUT_DAYS;

import com.google.common.collect.ImmutableMap;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplateColumn;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.SourceType;
import mw.gov.health.lmis.migration.tool.scm.repository.SystemDefaultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequsitionUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequsitionUtil.class);

  private final Integer numberOfPeriodsToAverage;

  /**
   * Creates new instance of this class.
   *
   * @param systemDefaultRepository repository that will receive system configuration settings
   *                                from Supply Chain Manager.
   */
  @Autowired
  public RequsitionUtil(SystemDefaultRepository systemDefaultRepository) {
    Short currentNumberOfPeriodsToAverage = systemDefaultRepository
        .findAll()
        .iterator()
        .next()
        .getNumberOfPeriodsToAverage();

    this.numberOfPeriodsToAverage = Optional
        .ofNullable(currentNumberOfPeriodsToAverage)
        .orElse((short) 2)
        .intValue();
  }

  /**
   * Creates new template for the givne program.
   */
  public RequisitionTemplate createTemplate(UUID programId) {
    LOGGER.info("Create requisition template for program: {}", programId);

    RequisitionTemplate template = new RequisitionTemplate();
    template.setProgramId(programId);
    template.setNumberOfPeriodsToAverage(numberOfPeriodsToAverage);
    template.setColumnsMap(getRequisitionTemplateColumnMap());

    return template;
  }

  private Map<String, RequisitionTemplateColumn> getRequisitionTemplateColumnMap() {
    return ImmutableMap
        .<String, RequisitionTemplateColumn>builder()
        .put(SKIPPED_COLUMN, create(SKIPPED_COLUMN, SourceType.USER_INPUT, false))
        .put(PRODUCT_CODE, create(PRODUCT_CODE, SourceType.REFERENCE_DATA, true))
        .put(PRODUCT, create(PRODUCT, SourceType.REFERENCE_DATA, true))
        .put(BEGINNING_BALANCE, create(BEGINNING_BALANCE, SourceType.USER_INPUT, false))
        .put(TOTAL_RECEIVED_QUANTITY, create(TOTAL_RECEIVED_QUANTITY, SourceType.USER_INPUT, true))
        .put(TOTAL_CONSUMED_QUANTITY, create(TOTAL_CONSUMED_QUANTITY, SourceType.USER_INPUT, true))
        .put(TOTAL_LOSSES_AND_ADJUSTMENTS, create(TOTAL_LOSSES_AND_ADJUSTMENTS, SourceType.USER_INPUT, true))
        .put(TOTAL_STOCKOUT_DAYS, create(TOTAL_STOCKOUT_DAYS, SourceType.USER_INPUT, true))
        .put(STOCK_ON_HAND, create(STOCK_ON_HAND, SourceType.USER_INPUT, true))
        .put(AVERAGE_CONSUMPTION, create(AVERAGE_CONSUMPTION, SourceType.CALCULATED, false))
        .put(CALCULATED_ORDER_QUANTITY, create(CALCULATED_ORDER_QUANTITY, SourceType.CALCULATED, true))
        .put(PRICE_PER_PACK, create(PRICE_PER_PACK, SourceType.REFERENCE_DATA, false))
        .put(DISPENSING_UNIT, create(DISPENSING_UNIT, SourceType.REFERENCE_DATA, false))
        .put(REQUESTED_QUANTITY, create(REQUESTED_QUANTITY, SourceType.USER_INPUT, true))
        .put(
            REQUESTED_QUANTITY_EXPLANATION,
            create(REQUESTED_QUANTITY_EXPLANATION, SourceType.USER_INPUT, true)
        )
        .put(APPROVED_QUANTITY, create(APPROVED_QUANTITY, SourceType.USER_INPUT, true))
        .put(REMARKS_COLUMN, create(REMARKS_COLUMN, SourceType.USER_INPUT, false))
        .put(TOTAL_COLUMN, create(TOTAL_COLUMN, SourceType.CALCULATED, false))
        .put(PACKS_TO_SHIP, create(PACKS_TO_SHIP, SourceType.CALCULATED, false))
        .put(NUMBER_OF_NEW_PATIENTS_ADDED, create(NUMBER_OF_NEW_PATIENTS_ADDED, SourceType.USER_INPUT, false))
        .put(TOTAL_COST, create(TOTAL_COST, SourceType.CALCULATED, false))
        .put(ADJUSTED_CONSUMPTION, create(ADJUSTED_CONSUMPTION, SourceType.CALCULATED, true))
        .put(MAXIMUM_STOCK_QUANTITY, create(MAXIMUM_STOCK_QUANTITY, SourceType.CALCULATED, false))
        .build();
  }

  private RequisitionTemplateColumn create(String name, SourceType source, boolean displayed) {
    RequisitionTemplateColumn requisitionTemplateColumn = new RequisitionTemplateColumn();
    requisitionTemplateColumn.setName(name);
    requisitionTemplateColumn.setIsDisplayed(displayed);
    requisitionTemplateColumn.setSource(source);
    requisitionTemplateColumn.setLabel(name.toLowerCase(Locale.ENGLISH));

    return requisitionTemplateColumn;
  }

}
