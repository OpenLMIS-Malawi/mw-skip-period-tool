package org.openlmis.migration.tool.scm.repository;


import org.openlmis.migration.tool.scm.domain.Facility;
import org.openlmis.migration.tool.scm.domain.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends ReadOnlyRepository<Item, Integer> {

  List<Item> findByProcessingDateAndFacility(LocalDateTime processingDate, Facility facility);

  Item findByProcessingDateAndFacilityAndProductName(LocalDateTime processingDate,
                                                     Facility facility, String productName);

  Item findByProductName(String productName);

}
