package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.repository.CrudRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicLevel;

import java.util.UUID;

public interface GeographicLevelRepository
    extends CrudRepository<GeographicLevel, UUID> {
}
