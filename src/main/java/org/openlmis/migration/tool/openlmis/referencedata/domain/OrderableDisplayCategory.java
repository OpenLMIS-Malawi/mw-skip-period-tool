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

package org.openlmis.migration.tool.openlmis.referencedata.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openlmis.migration.tool.openlmis.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Informative category a Product is in when assigned to a {@link Program}.
 */
@Entity
@Table(name = "orderable_display_categories", schema = "referencedata")
@Getter
@Setter
@NoArgsConstructor
public class OrderableDisplayCategory extends BaseEntity {

  @Embedded
  private Code code;

  @Embedded
  @JsonUnwrapped
  private OrderedDisplayValue orderedDisplayValue;

  /**
   * Update this from another.  Copies display values from the other OrderableDisplayCategory
   * into this one.
   *
   * @param orderableDisplayCategory OrderableDisplayCategory to update from.
   */
  public void updateFrom(OrderableDisplayCategory orderableDisplayCategory) {
    this.orderedDisplayValue = orderableDisplayCategory.orderedDisplayValue;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof OrderableDisplayCategory)) {
      return false;
    }
    OrderableDisplayCategory that = (OrderableDisplayCategory) obj;
    return Objects.equals(code, that.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
}
