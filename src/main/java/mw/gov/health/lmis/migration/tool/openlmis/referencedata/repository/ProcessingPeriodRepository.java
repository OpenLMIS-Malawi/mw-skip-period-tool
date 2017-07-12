package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;

import java.util.UUID;

public interface ProcessingPeriodRepository extends JpaRepository<ProcessingPeriod, UUID> {

}
