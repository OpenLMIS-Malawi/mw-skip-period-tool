package mw.gov.health.lmis.migration.tool.batch;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.OpenLmisNumberUtils.isNotZero;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DuplicateProcessor implements ItemProcessor<List<Requisition>, List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateProcessor.class);
  private static final PropertyDescriptor[] RLI_DESCRIPTORS;
  private static final String[] FIELDS = new String[]{
      "beginningBalance", "totalReceivedQuantity", "totalLossesAndAdjustments", "stockOnHand",
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

  @Override
  public List<Requisition> process(List<Requisition> item) throws Exception {
    return item
        .stream()
        .filter(this::isEmpty)
        .collect(Collectors.toList());
  }

  private boolean isEmpty(Requisition requisition) {
    List<RequisitionLineItem> lines = requisition.getRequisitionLineItems();
    int lineCount = lines.size();
    int emptyLineCount = (int) lines.stream().filter(this::isEmpty).count();
    boolean empty = lineCount == emptyLineCount;

    if (empty) {
      LOGGER.info("Found empty requisition (all line items have zero values for all columns)");
    } else {
      LOGGER.info("Requisition contains non-zero data. Skipping...");
    }

    return empty;
  }

  private boolean isEmpty(RequisitionLineItem line) {
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

  private boolean containsValue(RequisitionLineItem line, PropertyDescriptor descriptor)
      throws IllegalAccessException, InvocationTargetException {
    Class<?> propertyType = descriptor.getPropertyType();
    Number value = Number.class.isAssignableFrom(propertyType)
        ? (Number) descriptor.getReadMethod().invoke(line)
        : 0;

    if (value instanceof Integer && isNotZero((Integer) value)
        || value instanceof Long && isNotZero((Long) value)
        || value instanceof BigDecimal && isNotZero((BigDecimal) value)) {
      LOGGER.info("The '{}' field contains a non-zero value", descriptor.getName());
      return true;
    }

    return false;
  }

}
