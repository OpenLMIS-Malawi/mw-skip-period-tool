package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;

import java.util.UUID;

public interface ProgramRepository extends JpaRepository<Program, UUID> {

  Program findByCode(Code code);

}
