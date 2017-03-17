package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.InMemoryRepository;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Facility;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OpenLmisFacilityRepository extends InMemoryRepository<Facility> {

  /**
   * Find correct OpenLMIS facility based on name and code.
   */
  public Facility findByNameAndCode(String name, String code) {
    Facility found = database
        .values()
        .stream()
        .filter(facility -> name.equals(facility.getName()) && code.equals(facility.getCode()))
        .findFirst()
        .orElse(null);

    if (null == found) {
      save(create(name, code));
      return findByNameAndCode(name, code);
    }

    return found;
  }

  private Facility create(String name, String code) {
    Facility facility = new Facility();
    facility.setId(UUID.randomUUID());
    facility.setName(name);
    facility.setCode(code);

    return facility;
  }

}
