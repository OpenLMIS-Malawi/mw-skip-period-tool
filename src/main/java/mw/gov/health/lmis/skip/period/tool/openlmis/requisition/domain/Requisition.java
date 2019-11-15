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

import static mw.gov.health.lmis.skip.period.tool.openlmis.requisition.domain.Requisition.SCHEMA;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "requisitions", schema = SCHEMA)
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Requisition extends BaseTimestampedEntity {

  public static final String SCHEMA = "requisition";

  @OneToMany(
      mappedBy = "requisition",
      cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE},
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  @Getter
  @Setter
  private List<RequisitionLineItem> requisitionLineItems;

  @ManyToOne
  @JoinColumn(name = "templateId", nullable = false)
  @Getter
  @Setter
  private RequisitionTemplate template;

  @Column(nullable = false)
  @Getter
  @Setter
  @Type(type = UUID_TYPE)
  private UUID facilityId;

  @Column(nullable = false)
  @Getter
  @Setter
  @Type(type = UUID_TYPE)
  private UUID programId;

  @Column(nullable = false)
  @Getter
  @Setter
  @Type(type = UUID_TYPE)
  private UUID processingPeriodId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  private RequisitionStatus status;

  @OneToMany(
      mappedBy = SCHEMA,
      cascade = CascadeType.ALL)
  @Getter
  @Setter
  private List<StatusChange> statusChanges = new ArrayList<>();

  @Column(nullable = false)
  @Getter
  @Setter
  private Boolean emergency;

  @Column(nullable = false)
  @Getter
  @Setter
  private Integer numberOfMonthsInPeriod;

  @Getter
  @Setter
  @Type(type = UUID_TYPE)
  private UUID supervisoryNodeId;

  @ManyToMany
  @JoinTable(name = "requisitions_previous_requisitions",
      schema = SCHEMA,
      joinColumns = {@JoinColumn(name = "requisitionId")},
      inverseJoinColumns = {@JoinColumn(name = "previousRequisitionId")})
  @Getter
  @Setter
  private List<Requisition> previousRequisitions;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "available_products", schema = SCHEMA,
      joinColumns = @JoinColumn(name = "requisitionId"))
  @Getter
  @Setter
  private Set<ApprovedProductReference> availableProducts;
}
