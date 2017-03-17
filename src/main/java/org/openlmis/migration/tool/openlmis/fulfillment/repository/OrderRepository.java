package org.openlmis.migration.tool.openlmis.fulfillment.repository;

import org.openlmis.migration.tool.openlmis.InMemoryRepository;
import org.openlmis.migration.tool.openlmis.fulfillment.domain.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository extends InMemoryRepository<Order> {
}
