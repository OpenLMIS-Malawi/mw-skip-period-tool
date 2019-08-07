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

package mw.gov.health.lmis.skip.period.tool.openlmis.requisition.domain;

import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.UnusedPrivateField"})
@Entity
@Table(name = "requisition_templates", schema = "requisition")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"programId", "facilityTypeIds"})
public class RequisitionTemplate extends BaseTimestampedEntity {

  @Getter
  private Integer numberOfPeriodsToAverage;

  @Getter
  private boolean populateStockOnHandFromStockCards;
  
  @Getter
  private String name;

  @Column(nullable = false)
  @Getter
  private boolean archived = false;

  @ElementCollection(fetch = FetchType.LAZY)
  @MapKeyColumn(name = "key")
  @Column(name = "value")
  @CollectionTable(
      name = "columns_maps",
      joinColumns = @JoinColumn(name = "requisitionTemplateId"))
  private Map<String, RequisitionTemplateColumn> columnsMap = new HashMap<>();

  @OneToMany(
      cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE},
      orphanRemoval = true,
      mappedBy = "template")
  private Set<RequisitionTemplateAssignment> templateAssignments = new HashSet<>();

  @Transient
  @Getter
  private UUID programId;

  @Transient
  @Getter
  private Set<UUID> facilityTypeIds = Sets.newHashSet();

  private void moveDownAllColumnsBetweenIndexes(int beginIndex, int endIndex) {
    for (RequisitionTemplateColumn column : columnsMap.values()) {
      if (column.getDisplayOrder() >= beginIndex && column.getDisplayOrder() < endIndex) {
        column.setDisplayOrder(column.getDisplayOrder() + 1);
      }
    }
  }

  /**
   * Finds a column by column name or throws exception.
   *
   * @param name name of requisition column.
   * @return {@link RequisitionTemplateColumn} if found column with the given name.
   */
  public RequisitionTemplateColumn findColumn(String name) {
    RequisitionTemplateColumn column = getRequisitionTemplateColumn(name);
    if (column == null) {
      throw new NullPointerException();
    }
    return column;
  }

  private RequisitionTemplateColumn getRequisitionTemplateColumn(String name) {
    if (columnsMap == null) {
      throw new NullPointerException();
    }
    return columnsMap.get(name);
  }
}
