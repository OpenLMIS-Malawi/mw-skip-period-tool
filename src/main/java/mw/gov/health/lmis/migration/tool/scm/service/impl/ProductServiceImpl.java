package mw.gov.health.lmis.migration.tool.scm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.Product;
import mw.gov.health.lmis.migration.tool.scm.repository.ProductAccessRepository;
import mw.gov.health.lmis.migration.tool.scm.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

  @Autowired
  private ProductAccessRepository productRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public String getProductCode(Integer productId) {
    Product product = productRepository.findById(productId);
    return MappingHelper.getProductCode(
        toolProperties, product.getName().trim(), product.getProductId().trim()
    );
  }

}
