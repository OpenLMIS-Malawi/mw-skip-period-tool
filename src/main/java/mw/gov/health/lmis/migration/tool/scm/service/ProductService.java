package mw.gov.health.lmis.migration.tool.scm.service;

import java.util.Optional;

public interface ProductService {

  Optional<String> getProductCode(Integer id);
  
}
