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

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mw.gov.health.lmis.skip.period.tool.openlmis.ExternalStatus;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "status_changes", schema = "requisition")
@NoArgsConstructor
public class StatusChange extends BaseTimestampedEntity {

  @ManyToOne(cascade = {CascadeType.REFRESH})
  @JoinColumn(name = "requisitionId", nullable = false)
  @Getter
  @Setter
  private Requisition requisition;

  @Getter
  @Setter
  @Type(type = UUID_TYPE)
  private UUID authorId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  private ExternalStatus status;

  private StatusChange(Requisition requisition, UUID authorId, ExternalStatus status) {
    this.requisition = Objects.requireNonNull(requisition);
    this.authorId = authorId;
    this.status = Objects.requireNonNull(status);
    this.setCreatedDate(requisition.getCreatedDate());
    this.setModifiedDate(requisition.getModifiedDate());
  }

  public static StatusChange newStatusChange(Requisition requisition, UUID authorId) {
    return new StatusChange(requisition, authorId, requisition.getStatus());
  }

  public static StatusChange newStatusChange(Requisition requisition, UUID authorId,
                                             ExternalStatus status) {
    return new StatusChange(requisition, authorId, status);
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setCreatedDate(getCreatedDate());
    exporter.setStatus(status);
    exporter.setAuthorId(authorId);
  }

  public interface Exporter {

    void setCreatedDate(ZonedDateTime createdDate);

    void setStatus(ExternalStatus status);

    void setAuthorId(UUID authorId);
  }
}
