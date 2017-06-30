package mw.gov.health.lmis.migration.tool.scm.repository;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.OpenLmisNumberUtils.isNotZero;

import com.google.common.collect.Maps;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class AdjustmentAccessRepository extends PreparedAccessRepository<Adjustment> {

  @Override
  boolean isNotValid(Adjustment element) {
    return !isNotZero(element.getQuantity());
  }

  @Override
  Integer mapToKey(Adjustment element) {
    return element.getItem();
  }

  /**
   * Finds all adjustments for the given item.
   */
  public Map<Integer, List<Adjustment>> search(List<Integer> itemIds) {
    List<Integer> filtered = getValid(itemIds);

    if (filtered.isEmpty()) {
      return Maps.newHashMap();
    }

    return findAll(
        elem -> isNotZero(elem.getQuantity()) && filtered.contains(elem.getItem()),
        new SearchDetails(filtered))
        .stream()
        .collect(Collectors.groupingBy(Adjustment::getItem));
  }

  @Override
  String getTableName() {
    return properties.getTableNames().getAdjustment();
  }

  @Override
  Adjustment mapRow(Row row) {
    return new Adjustment(row);
  }

}
