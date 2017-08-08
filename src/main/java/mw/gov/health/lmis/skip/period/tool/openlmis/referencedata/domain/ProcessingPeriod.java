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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mw.gov.health.lmis.skip.period.tool.openlmis.BaseEntity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "processing_periods", schema = "referencedata")
@NoArgsConstructor
public class ProcessingPeriod extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "processingScheduleId", nullable = false)
  @Getter
  @Setter
  private ProcessingSchedule processingSchedule;

  @Column(nullable = false, columnDefinition = "text")
  @Getter
  @Setter
  private String name;

  @Column(columnDefinition = "text")
  @Getter
  @Setter
  private String description;

  @Column(nullable = false)
  @Getter
  @Setter
  private LocalDate startDate;

  @Column(nullable = false)
  @Getter
  @Setter
  private LocalDate endDate;

  private ProcessingPeriod(String name, ProcessingSchedule schedule,
                           LocalDate startDate, LocalDate endDate) {
    this.processingSchedule = schedule;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static ProcessingPeriod newPeriod(String name, ProcessingSchedule schedule,
                                            LocalDate startDate, LocalDate endDate) {
    return new ProcessingPeriod(name, schedule, startDate, endDate);
  }

  /**
   * Construct new processing period based on an importer (DTO).
   *
   * @param importer importer (DTO) to use
   * @return new processing period
   */
  public static ProcessingPeriod newPeriod(Importer importer) {
    ProcessingPeriod newPeriod = new ProcessingPeriod(
          importer.getName(),
          importer.getProcessingSchedule(),
          importer.getStartDate(),
          importer.getEndDate());
    newPeriod.id = importer.getId();
    newPeriod.description = importer.getDescription();
    return newPeriod;
  }

  /**
   * Returns duration of period in months.
   *
   * @return number od months.
   */
  public int getDurationInMonths() {
    Period length = Period.between(startDate, endDate);
    int months = length.getMonths();
    months += length.getYears() * 12;
    if (length.getDays() >= 15 || months == 0) {
      months++;
    }

    return months;
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setName(name);
    exporter.setProcessingSchedule(processingSchedule);
    exporter.setDescription(description);
    exporter.setStartDate(startDate);
    exporter.setEndDate(endDate);
    exporter.setDurationInMonths(getDurationInMonths());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, processingSchedule);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ProcessingPeriod)) {
      return false;
    }
    ProcessingPeriod period = (ProcessingPeriod) obj;
    return Objects.equals(name, period.name)
          && Objects.equals(processingSchedule, period.processingSchedule);
  }

  public interface Exporter {
    void setId(UUID id);

    void setName(String name);

    void setProcessingSchedule(ProcessingSchedule schedule);

    void setDescription(String description);

    void setStartDate(LocalDate startDate);

    void setEndDate(LocalDate endDate);

    void setDurationInMonths(Integer duration);
  }

  public interface Importer {
    UUID getId();

    String getName();

    ProcessingSchedule getProcessingSchedule();

    String getDescription();

    LocalDate getStartDate();

    LocalDate getEndDate();
  }

}
