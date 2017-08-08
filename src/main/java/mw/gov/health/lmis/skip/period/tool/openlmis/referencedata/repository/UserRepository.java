package mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.repository;

import mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.domain.User;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

  User findByUsername(String username);

}
