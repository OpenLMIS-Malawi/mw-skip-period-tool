package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.Code;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisProgramRepository extends CrudRepository<Program, UUID> {

  Program findByName(String name);

  Program findByCode(Code code);

}
