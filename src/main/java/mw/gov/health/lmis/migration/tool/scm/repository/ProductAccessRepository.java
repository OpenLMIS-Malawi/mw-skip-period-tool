package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Product;

@Repository
public class ProductAccessRepository extends BaseAccessRepository<Product> {

  @Override
  public String getTableName() {
    return properties.getTableNames().getProduct();
  }

  @Override
  Product mapRow(Row row) {
    return new Product(row);
  }

  @Cacheable("products_tmp")
  public Product findByProductId(String productId) {
    return find("strProductID", productId);
  }

  @Cacheable("products")
  public Product findById(Integer id) {
    return find("Pr_lngProductID", id);
  }

}
