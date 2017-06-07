/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mw.gov.health.lmis.migration.tool.openlmis.BaseEntity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * CommodityTypes are generic commodities to simplify ordering and use.  A CommodityType doesn't
 * have a single manufacturer, nor a specific packaging.  Instead a CommodityType represents a
 * refined categorization of products that may typically be ordered / exchanged for one another.
 */
@Entity
@Table(name = "commodity_types", schema = "referencedata")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name", callSuper = false)
public final class CommodityType extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Getter
  @Column(nullable = false)
  private String classificationSystem;

  @Getter
  @Column(nullable = false)
  private String classificationId;

  @Getter
  @ManyToOne
  @JoinColumn(columnDefinition = "parentid")
  private CommodityType parent;

  @Getter
  @Setter
  @OneToMany(mappedBy = "parent")
  private List<CommodityType> children;

  /**
   * Validates and assigns a parent to this commodity type.
   * No cycles in the hierarchy are allowed.
   *
   * @param parent the parent to assign
   */
  public void assignParent(CommodityType parent) {
    validateIsNotDescendant(parent);

    this.parent = parent;
    parent.children.add(this);
  }

  private void validateIsNotDescendant(CommodityType commodityType) {
    for (CommodityType child : children) {
      if (child.equals(commodityType)) {
        throw new IllegalArgumentException();
      }
      child.validateIsNotDescendant(commodityType);
    }
  }

}
