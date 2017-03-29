package org.openlmis.migration.tool.openlmis.referencedata.repository;

import org.openlmis.migration.tool.openlmis.referencedata.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OlmisUserRepository extends CrudRepository<User, UUID> {

  User findByUsername(String username);

}
