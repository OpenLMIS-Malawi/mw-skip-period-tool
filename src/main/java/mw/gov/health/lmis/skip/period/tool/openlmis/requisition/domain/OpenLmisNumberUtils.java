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

import static java.math.BigDecimal.ZERO;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

public final class OpenLmisNumberUtils extends NumberUtils {

  private OpenLmisNumberUtils() {
    throw new UnsupportedOperationException();
  }

  public static int zeroIfNull(Integer value) {
    return defaultIfNull(value, 0);
  }

  public static long zeroIfNull(Long value) {
    return defaultIfNull(value, 0L);
  }

  public static short zeroIfNull(Short value) {
    return defaultIfNull(value, (short) 0);
  }

  public static BigDecimal zeroIfNull(BigDecimal value) {
    return defaultIfNull(value, ZERO);
  }

  public static boolean isNotZero(Integer value) {
    return null != value && 0 != value;
  }

  public static boolean isNotZero(Long value) {
    return null != value && 0L != value;
  }

  public static boolean isNotZero(BigDecimal value) {
    return !BigDecimal.ZERO.equals(value);
  }

  private static <N extends Number> N defaultIfNull(N number, N defaultNumber) {
    return ObjectUtils.defaultIfNull(number, defaultNumber);
  }

}
