package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.domain.Main;
import org.openlmis.migration.tool.openlmis.InMemoryRepository;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.springframework.stereotype.Service;

@Service
public class OpenLmisProgramRepository extends InMemoryRepository<Program> {

  /**
   * Finds program based on name.
   */
  public Program findByName(Main main) {
    String name = "MAIN-"
        + main.getId().getFacility().getCode()
        + "-"
        + main.getId().getProcessingDate().toLocalDate();

    Program found = database
        .values()
        .stream()
        .filter(program -> name.equals(program.getName()))
        .findFirst()
        .orElse(null);

    if (null == found) {
      save(create(name));
      return findByName(main);
    }

    return found;
  }

  private Program create(String name) {
    Program program = new Program();
    program.setName(name);

    return program;
  }

}
