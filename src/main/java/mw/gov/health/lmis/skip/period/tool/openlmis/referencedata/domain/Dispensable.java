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

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import mw.gov.health.lmis.skip.period.tool.openlmis.BaseEntity;

/**
 * A dispensable describes how product is dispensed/given to a patient.
 * Description of the dispensable contains information about product form,
 * dosage, dispensing unit etc.
 */
@Entity
@Table(name = "dispensables", schema = "referencedata")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("abstract")
public abstract class Dispensable extends BaseEntity {

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "key")
  @Column(name = "value")
  @CollectionTable(
      name = "dispensable_attributes",
      joinColumns = @JoinColumn(name = "dispensableid"))
  @Getter
  protected Map<String, String> attributes;

  protected Dispensable() {
    attributes = new HashMap<>();
  }

  public abstract boolean equals(Object object);

  public abstract int hashCode();

  public abstract String toString();
}