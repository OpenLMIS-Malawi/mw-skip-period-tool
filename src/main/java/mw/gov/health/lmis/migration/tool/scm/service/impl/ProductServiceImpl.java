package mw.gov.health.lmis.migration.tool.scm.service.impl;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.Product;
import mw.gov.health.lmis.migration.tool.scm.repository.ProductAccessRepository;
import mw.gov.health.lmis.migration.tool.scm.service.ProductService;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

  @Autowired
  private ProductAccessRepository productRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public Optional<String> getProductCode(Integer id) {
    Product product = productRepository.findById(id);

    if (null == product) {
      LOGGER.error("Can't find product with id: {}", id);
      return Optional.empty();
    }

    String productName = trimToEmpty(product.getName());
    String productId = trimToEmpty(product.getProductId());

    return Optional.ofNullable(
        MappingHelper.getProductCode(toolProperties, productName, productId)
    );
  }

}
