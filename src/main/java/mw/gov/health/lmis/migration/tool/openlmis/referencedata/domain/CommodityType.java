package mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * CommodityTypes are generic commodities to simplify ordering and use.  A CommodityType doesn't
 * have a single manufacturer, nor a specific packaging.  Instead a CommodityType represents a
 * refined categorization of products that may typically be ordered / exchanged for one another.
 */
@Entity
@DiscriminatorValue("COMMODITY_TYPE")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CommodityType extends Orderable {

  private String description;

  @Getter
  private String classificationSystem;

  @Getter
  private String classificationId;

  @Getter
  @ManyToOne
  @JoinColumn(columnDefinition = "parentid")
  private CommodityType parent;

  @Getter
  @Setter
  @OneToMany(mappedBy = "parent")
  @JsonIgnore
  private List<CommodityType> children;

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean canFulfill(Orderable product) {
    return this.equals(product);
  }
}
