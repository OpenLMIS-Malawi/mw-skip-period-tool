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
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.SourceType.CALCULATED;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.SourceType.REFERENCE_DATA;
import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.SourceType.USER_INPUT;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.AvailableRequisitionColumn;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplateColumn;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.SourceType;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisAvailableRequisitionColumnRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.SystemDefaultRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequsitionUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequsitionUtil.class);

  private final Integer numberOfPeriodsToAverage;

  @Autowired
  private OlmisAvailableRequisitionColumnRepository olmisAvailableRequisitionColumnRepository;

  /**
   * Creates new instance of this class.
   *
   * @param systemDefaultRepository repository that will receive system configuration settings from
   *                                Supply Chain Manager.
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
        .put(
            PRODUCT_CODE,
            create(
                "orderable.productCode", "Product", REFERENCE_DATA, true,
                "bde01507-3837-47b7-ae08-cec92c0c3cd2", 1
            )
        )
        .put(
            PRODUCT,
            create(
                "orderable.fullProductName", "Product Description", REFERENCE_DATA, true,
                "e53e80de-fc63-4ecb-b6b2-ef376b34c926", 2
            )
        )
        .put(
            STOCK_ON_HAND,
            create(
                STOCK_ON_HAND, "Stock on Hand", USER_INPUT, true,
                "752cda76-0db5-4b6e-bb79-0f531ab78e2c", 3
            )
        )

        .put(
            TOTAL_LOSSES_AND_ADJUSTMENTS,
            create(
                TOTAL_LOSSES_AND_ADJUSTMENTS, "Adjustment", USER_INPUT, true,
                "cd57f329-f549-4717-882e-ecbf98122c38", 4
            )
        )
        .put(
            TOTAL_CONSUMED_QUANTITY,
            create(
                TOTAL_CONSUMED_QUANTITY, "Quantity Consumed", USER_INPUT, true,
                "9e825396-269d-4873-baa4-89054e2722f4", 5
            )
        )
        .put(
            TOTAL_STOCKOUT_DAYS,
            create(
                TOTAL_STOCKOUT_DAYS, "Stocked Out Days", USER_INPUT, true,
                "750b9359-c097-4612-8328-d21671f88920", 6
            )
        )
        .put(
            ADJUSTED_CONSUMPTION,
            create(
                ADJUSTED_CONSUMPTION, "Adjusted Consumption", CALCULATED, true,
                "720dd95b-b765-4afb-b7f2-7b22261c32f3", 7
            )
        )
        .put(
            CALCULATED_ORDER_QUANTITY,
            create(
                CALCULATED_ORDER_QUANTITY, "Calculated Quantity", CALCULATED, true,
                "5528576b-b1e7-48d9-bf32-fd0eefefaa9a", 8
            )
        )
        .put(
            REQUESTED_QUANTITY,
            create(
                REQUESTED_QUANTITY, "Reorder Quantity", USER_INPUT, true,
                "4a2e9fd3-1127-4b68-9912-84a5c00f6999", 9
            )
        )
        .put(
            REQUESTED_QUANTITY_EXPLANATION,
            create(
                REQUESTED_QUANTITY_EXPLANATION, "Reorder Quantity explanation", USER_INPUT, true,
                "6b8d331b-a0dd-4a1f-aafb-40e6a72ab9f5", 10
            )
        )
        .put(
            APPROVED_QUANTITY,
            create(
                APPROVED_QUANTITY, "Approved Quantity", USER_INPUT, true,
                "a62a5fed-c0b6-4d49-8a96-c631da0d0113", 11
            )
        )
        .put(
            TOTAL_RECEIVED_QUANTITY,
            create(
                TOTAL_RECEIVED_QUANTITY, "Receipts", USER_INPUT, true,
                "5ba8b72d-277a-4da8-b10a-23f0cda23cb4", 12
            )
        )
        .put(
            SKIPPED_COLUMN,
            create(
                SKIPPED_COLUMN, "Skip", USER_INPUT, false,
                "c6dffdee-3813-40d9-8737-f531d5adf420", 13
            )
        )
        .put(
            BEGINNING_BALANCE,
            create(
                BEGINNING_BALANCE, "Beginning balance", USER_INPUT, false,
                "33b2d2e9-3167-46b0-95d4-1295be9afc22", 14
            )
        )
        .put(
            AVERAGE_CONSUMPTION,
            create(
                AVERAGE_CONSUMPTION, "Average consumption", CALCULATED, false,
                "89113ec3-40e9-4d81-9516-b56adba7f8cd", 15
            )
        )
        .put(
            PRICE_PER_PACK,
            create(
                PRICE_PER_PACK, "Price per pack", REFERENCE_DATA, false,
                "df524868-9d0a-18e6-80f5-76304ded7ab9", 16
            )
        )
        .put(
            DISPENSING_UNIT,
            create(
                "orderable.dispensable.dispensingUnit", "Dispensing Unit", REFERENCE_DATA, false,
                "61e6d059-10ef-40c4-a6e3-fa7b9ad741ec", 17
            )
        )
        .put(
            REMARKS_COLUMN,
            create(
                REMARKS_COLUMN, "Remarks", USER_INPUT, false,
                "2ed8c74a-f424-4742-bd14-cfbe67b6e7be", 18
            )
        )
        .put(
            TOTAL_COLUMN,
            create(
                TOTAL_COLUMN, "Total", CALCULATED, false,
                "ef524868-9d0a-11e6-80f5-76304dec7eb7", 19
            )
        )
        .put(
            PACKS_TO_SHIP,
            create(
                PACKS_TO_SHIP, "Packs to ship", CALCULATED, false,
                "dc9dde56-593d-4929-81be-d1faec7025a8", 20
            )
        )
        .put(
            NUMBER_OF_NEW_PATIENTS_ADDED,
            create(
                NUMBER_OF_NEW_PATIENTS_ADDED, "Number of new patients added", USER_INPUT, false,
                "5708ebf9-9317-4420-85aa-71b2ae92643d", 21
            )
        )
        .put(
            TOTAL_COST,
            create(
                TOTAL_COST, "Total cost", CALCULATED, false,
                "e3a0c1fc-c2d5-11e6-af2d-3417eb83144e", 22
            )
        )
        .put(
            MAXIMUM_STOCK_QUANTITY,
            create(
                MAXIMUM_STOCK_QUANTITY, "Maximum stock quantity", CALCULATED, false,
                "913e1a4f-f3b0-40c6-a422-2f73608c6f3d", 23
            )
        )
        .build();
  }

  private RequisitionTemplateColumn create(String name, String label, SourceType source,
                                           boolean displayed, String columnDefinitionId,
                                           int displayOrder) {
    AvailableRequisitionColumn columnDefinition = olmisAvailableRequisitionColumnRepository
        .findOne(UUID.fromString(columnDefinitionId));

    RequisitionTemplateColumn requisitionTemplateColumn = new RequisitionTemplateColumn();
    requisitionTemplateColumn.setName(name);
    requisitionTemplateColumn.setLabel(label);
    requisitionTemplateColumn.setIndicator(columnDefinition.getIndicator());
    requisitionTemplateColumn.setDisplayOrder(displayOrder);
    requisitionTemplateColumn.setIsDisplayed(displayed);
    requisitionTemplateColumn.setSource(source);
    requisitionTemplateColumn.setDefinition(columnDefinition.getDefinition());
    requisitionTemplateColumn.setColumnDefinition(columnDefinition);

    if (!isEmpty(columnDefinition.getOptions())) {
      requisitionTemplateColumn.setOption(columnDefinition.getOptions().iterator().next());
    }

    return requisitionTemplateColumn;
  }

}
