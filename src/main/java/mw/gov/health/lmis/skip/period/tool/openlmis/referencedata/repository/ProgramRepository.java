package mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.repository;

import java.util.UUID;
import mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, UUID> {

}
