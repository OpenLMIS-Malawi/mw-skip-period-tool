package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.openlmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisStockAdjustmentReasonRepository
    extends CrudRepository<StockAdjustmentReason, UUID> {

  StockAdjustmentReason findByProgramAndName(Program program, String name);

}
