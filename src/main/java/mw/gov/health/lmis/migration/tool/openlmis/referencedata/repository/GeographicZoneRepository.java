package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicZone;

import java.util.UUID;

public interface GeographicZoneRepository
    extends CrudRepository<GeographicZone, UUID> {
}
