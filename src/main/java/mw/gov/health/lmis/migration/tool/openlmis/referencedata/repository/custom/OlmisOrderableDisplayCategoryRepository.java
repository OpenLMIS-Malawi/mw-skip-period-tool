package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.custom;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OlmisOrderableDisplayCategoryRepository
    extends CrudRepository<OrderableDisplayCategory, UUID> {

  @Query("FROM OrderableDisplayCategory WHERE orderedDisplayValue.displayName = :displayName")
  OrderableDisplayCategory findByDisplayName(@Param("displayName") String displayName);
}
