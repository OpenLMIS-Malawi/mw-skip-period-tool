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
@Table(name = "status_messages", schema = "requisition")
@NoArgsConstructor
public class StatusMessage extends BaseTimestampedEntity {

  @ManyToOne(cascade = {CascadeType.REFRESH})
  @JoinColumn(name = "requisitionId", nullable = false)
  @Getter
  @Setter
  private Requisition requisition;

  @Getter
  @Setter
  @Type(type = UUID_TYPE)
  private UUID authorId;

  @Getter
  @Setter
  private String authorFirstName;

  @Getter
  @Setter
  private String authorLastName;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  private ExternalStatus status;

  @Column(nullable = false)
  @Getter
  @Setter
  private String body;

  private StatusMessage(Requisition requisition, UUID authorId, String authorFirstName,
                        String authorLastName, String body, ExternalStatus status) {
    this.requisition = Objects.requireNonNull(requisition);
    this.authorId = authorId;
    this.authorFirstName = authorFirstName;
    this.authorLastName = authorLastName;
    this.status = Objects.requireNonNull(status);
    this.body = Objects.requireNonNull(body);
    this.setCreatedDate(requisition.getCreatedDate());
    this.setModifiedDate(requisition.getModifiedDate());
  }

  public static StatusMessage newStatusMessage(Requisition requisition, UUID authorId,
                                               String authorFirstName, String authorLastName,
                                               String body, ExternalStatus status) {
    return new StatusMessage(requisition, authorId, authorFirstName, authorLastName, body, status);
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setAuthorId(authorId);
    exporter.setAuthorFirstName(authorFirstName);
    exporter.setAuthorLastName(authorLastName);
    exporter.setRequisitionId(requisition.getId());
    exporter.setStatus(status);
    exporter.setBody(body);
    exporter.setCreatedDate(getCreatedDate());

  }

  public interface Exporter {
    void setId(UUID id);

    void setAuthorId(UUID authorId);

    void setAuthorFirstName(String authorFirstName);

    void setAuthorLastName(String authorLastName);

    void setRequisitionId(UUID requisitionId);

    void setBody(String body);

    void setStatus(ExternalStatus status);

    void setCreatedDate(ZonedDateTime createdDate);
  }
}
