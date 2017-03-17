package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.domain.Item;
import org.openlmis.migration.tool.openlmis.InMemoryRepository;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Code;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.openlmis.migration.tool.openlmis.referencedata.domain.TradeItem;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class OpenLmisOrderableRepository extends InMemoryRepository<Orderable> {

  /**
   * Finds orderable object by name.
   */
  public Orderable findByName(Item item) {
    Orderable found = database
        .values()
        .stream()
        .filter(orderable -> item.getProductName().equals(orderable.getName()))
        .findFirst()
        .orElse(null);

    if (null == found) {
      save(create(item));
      return findByName(item);
    }

    return found;
  }

  private Orderable create(Item item) {
    Orderable orderable = new TradeItem();
    orderable.setId(UUID.randomUUID());
    orderable.setProductCode(new Code(item.getProduct().getProductId()));
    orderable.setName(item.getProductName());

    return orderable;
  }

}
