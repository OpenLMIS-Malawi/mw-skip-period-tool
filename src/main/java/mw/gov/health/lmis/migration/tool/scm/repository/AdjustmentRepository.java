package mw.gov.health.lmis.migration.tool.scm.repository;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.OpenLmisNumberUtils.isNotZero;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class AdjustmentRepository extends BaseRepository<Adjustment> {

  /**
   * Finds all adjustmenets for the given item.
   */
  public Map<Integer, List<Adjustment>> search(List<Integer> itemIds) {
    return search(elem -> isNotZero(elem.getQuantity()) && itemIds.contains(elem.getItem()))
        .stream()
        .collect(Collectors.groupingBy(Adjustment::getItem));
  }

  @Override
  String getTableName() {
    return "CTF_Adjustments";
  }

  @Override
  Adjustment mapRow(Row row) {
    return new Adjustment(row);
  }
}
