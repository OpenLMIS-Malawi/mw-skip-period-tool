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

import java.time.LocalDate;
import java.time.Period;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mw.gov.health.lmis.skip.period.tool.openlmis.BaseEntity;

@Entity
@Table(name = "processing_periods", schema = "referencedata")
@NoArgsConstructor
public class ProcessingPeriod extends BaseEntity {

  @Column(nullable = false, columnDefinition = "text")
  @Getter
  @Setter
  private String name;

  @Column(nullable = false)
  @Getter
  @Setter
  private LocalDate startDate;

  @Column(nullable = false)
  @Getter
  @Setter
  private LocalDate endDate;

  /**
   * Returns duration of period in months.
   *
   * @return number of months.
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

}
