package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;

import java.util.UUID;

public interface OrderableDisplayCategoryRepository
    extends CrudRepository<OrderableDisplayCategory, UUID> {

  @Query("FROM OrderableDisplayCategory WHERE orderedDisplayValue.displayName = :displayName")
  OrderableDisplayCategory findByDisplayName(@Param("displayName") String displayName);
}
