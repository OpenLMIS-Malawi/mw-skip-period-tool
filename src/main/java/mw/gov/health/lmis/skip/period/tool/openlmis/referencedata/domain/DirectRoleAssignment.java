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

package mw.gov.health.lmis.skip.period.tool.openlmis.referencedata.domain;

import com.google.common.collect.Sets;

import lombok.NoArgsConstructor;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("direct")
@NoArgsConstructor
public class DirectRoleAssignment extends RoleAssignment {

  public DirectRoleAssignment(Role role, User user) {
    super(role, user);
  }

  @Override
  protected Set<RightType> getAcceptableRightTypes() {
    return Sets.newHashSet(RightType.GENERAL_ADMIN, RightType.REPORTS);
  }

  @Override
  public boolean hasRight(RightQuery rightQuery) {
    return role.contains(rightQuery.getRight());
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setRole(role);
  }
}
