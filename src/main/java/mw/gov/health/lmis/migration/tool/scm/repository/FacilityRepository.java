package mw.gov.health.lmis.migration.tool.scm.repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Facility;

public interface FacilityRepository extends ReadOnlyRepository<Facility, String> {

  Facility findByCode(String code);

}
