package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisFacilityRepository extends CrudRepository<Facility, UUID> {

  Facility findByCode(String code);

}
