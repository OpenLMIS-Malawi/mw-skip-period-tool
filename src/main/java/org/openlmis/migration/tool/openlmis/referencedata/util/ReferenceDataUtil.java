package org.openlmis.migration.tool.openlmis.referencedata.util;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import org.openlmis.migration.tool.openlmis.referencedata.domain.Code;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Facility;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.openlmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import org.openlmis.migration.tool.openlmis.referencedata.domain.TradeItem;
import org.openlmis.migration.tool.scm.domain.AdjustmentType;
import org.openlmis.migration.tool.scm.domain.Item;
import org.openlmis.migration.tool.scm.domain.Main;
import org.openlmis.migration.tool.scm.domain.SystemDefault;
import org.openlmis.migration.tool.scm.repository.SystemDefaultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ReferenceDataUtil {

  @Autowired
  private SystemDefaultRepository systemDefaultRepository;

  /**
   * Creates a new instance of OpenLMIS facility.
   */
  public Facility create(String name, String code) {
    Facility facility = new Facility();
    facility.setId(UUID.randomUUID());
    facility.setName(name);
    facility.setCode(code);
    facility.setActive(true);
    facility.setEnabled(true);

    return facility;
  }

  /**
   * Creates new orderable instance.
   */
  public Orderable create(Item item) {
    Orderable orderable = new TradeItem();
    orderable.setId(UUID.randomUUID());
    orderable.setProductCode(new Code(item.getProduct().getProductId()));
    orderable.setName(item.getProductName());

    return orderable;
  }

  /**
   * Creates processing period.
   */
  public ProcessingPeriod create(LocalDateTime dateTime) {
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
  public Program create(Main main) {
    Program program = new Program();
    program.setName(main.getProgramName());
    program.setCode(new Code(main.getProgramName().replace(' ', '_')));

    return program;
  }

  /**
   * Creates new stock adjustment reason.
   */
  public StockAdjustmentReason create(Program program, AdjustmentType adjustmentType) {
    StockAdjustmentReason reason = new StockAdjustmentReason();
    reason.setId(UUID.randomUUID());
    reason.setProgram(program);
    reason.setName(adjustmentType.getCode());
    reason.setDescription(adjustmentType.getName());
    reason.setAdditive(!adjustmentType.getNegative());

    return reason;
  }

}
