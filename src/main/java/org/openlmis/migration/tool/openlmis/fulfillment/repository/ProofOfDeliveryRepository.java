package org.openlmis.migration.tool.openlmis.fulfillment.repository;

import org.openlmis.migration.tool.openlmis.fulfillment.domain.ProofOfDelivery;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProofOfDeliveryRepository extends CrudRepository<ProofOfDelivery, UUID> {

}
