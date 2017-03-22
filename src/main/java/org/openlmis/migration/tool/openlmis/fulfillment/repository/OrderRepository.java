package org.openlmis.migration.tool.openlmis.fulfillment.repository;

import org.openlmis.migration.tool.openlmis.fulfillment.domain.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
  
}
