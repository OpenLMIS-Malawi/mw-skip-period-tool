package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;

import java.util.UUID;

public interface OlmisFacilityRepository extends CrudRepository<Facility, UUID> {

  Facility findByCode(String code);

  Facility findByName(String name);
  
}
