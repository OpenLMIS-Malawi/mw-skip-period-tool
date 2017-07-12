package mw.gov.health.lmis.migration.tool.openlmis.requisition.util;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.OpenLmisNumberUtils.isNotZero;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

public class RequisitionUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequisitionUtil.class);
  private static final PropertyDescriptor[] RLI_DESCRIPTORS;
  private static final String[] FIELDS = new String[]{
      "totalReceivedQuantity", "totalLossesAndAdjustments", "stockOnHand",
      "requestedQuantity", "totalConsumedQuantity", "total", "approvedQuantity",
      "totalStockoutDays", "packsToShip", "numberOfNewPatientsAdded", "adjustedConsumption",
      "averageConsumption", "maximumStockQuantity", "calculatedOrderQuantity"
  };

  static {
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(RequisitionLineItem.class);
      RLI_DESCRIPTORS = beanInfo.getPropertyDescriptors();
    } catch (IntrospectionException exp) {
      LOGGER.error("An exception occurs during introspection", exp);
      throw new ExceptionInInitializerError(exp);
    }
  }

  private RequisitionUtil() {
    throw new UnsupportedOperationException();
  }

  /**
   * Checks if the given requisition is empty. An empty requisition is when all line items contain
   * only zero value in all columns.
   */
  public static boolean isEmpty(Requisition requisition) {
    List<RequisitionLineItem> lines = requisition.getRequisitionLineItems();

    int lineCount = lines.size();
    int emptyLineCount = (int) lines.stream().filter(RequisitionUtil::isEmpty).count();

    return lineCount == emptyLineCount;
  }

  private static boolean isEmpty(RequisitionLineItem line) {
    try {
      for (PropertyDescriptor descriptor : RLI_DESCRIPTORS) {
        if (ArrayUtils.contains(FIELDS, descriptor.getName())
            && containsValue(line, descriptor)) {
          return false;
        }
      }
    } catch (IllegalAccessException | InvocationTargetException exp) {
      LOGGER.error("An exception occurs during reading the field", exp);
    }

    return true;
  }

  private static boolean containsValue(RequisitionLineItem line, PropertyDescriptor descriptor)
      throws IllegalAccessException, InvocationTargetException {
    Class<?> propertyType = descriptor.getPropertyType();
    Number value = Number.class.isAssignableFrom(propertyType)
        ? (Number) descriptor.getReadMethod().invoke(line)
        : 0;

    if (value instanceof Integer && isNotZero((Integer) value)
        || value instanceof Long && isNotZero((Long) value)
        || value instanceof BigDecimal && isNotZero((BigDecimal) value)) {
      LOGGER.debug("The '{}' field contains a non-zero value", descriptor.getName());
      return true;
    }

    return false;
  }

}
