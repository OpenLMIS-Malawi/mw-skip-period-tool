package mw.gov.health.lmis.migration.tool.scm.repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.repository.custom.MainRepositoryCustom;

public interface MainRepository extends ReadOnlyRepository<Main, Main.ComplexId>,
    MainRepositoryCustom {

}
