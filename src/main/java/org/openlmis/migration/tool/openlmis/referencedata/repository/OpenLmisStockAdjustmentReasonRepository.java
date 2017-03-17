package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.domain.AdjustmentType;
import org.openlmis.migration.tool.openlmis.InMemoryRepository;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.openlmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OpenLmisStockAdjustmentReasonRepository
    extends InMemoryRepository<StockAdjustmentReason> {

  /**
   * Finds stock adjustment reason based on program.
   */
  public StockAdjustmentReason findByProgram(Program program, AdjustmentType adjustmentType) {
    StockAdjustmentReason found = database
        .values()
        .stream()
        .filter(reason -> program.getId().equals(reason.getProgram().getId()))
        .findFirst()
        .orElse(null);

    if (null == found) {
      save(create(program, adjustmentType));
      return findByProgram(program, adjustmentType);
    }

    return found;
  }

  private StockAdjustmentReason create(Program programDto, AdjustmentType adjustmentType) {
    StockAdjustmentReason reason = new StockAdjustmentReason();
    reason.setId(UUID.randomUUID());
    reason.setProgram(programDto);
    reason.setName(adjustmentType.getCode());
    reason.setDescription(adjustmentType.getName());
    reason.setAdditive(!adjustmentType.getNegative());

    return reason;
  }

}
