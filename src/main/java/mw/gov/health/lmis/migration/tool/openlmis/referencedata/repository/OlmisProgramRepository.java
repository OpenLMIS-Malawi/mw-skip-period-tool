package mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisProgramRepository extends CrudRepository<Program, UUID> {

  Program findByName(String name);

  Program findByCode(Code code);

}
