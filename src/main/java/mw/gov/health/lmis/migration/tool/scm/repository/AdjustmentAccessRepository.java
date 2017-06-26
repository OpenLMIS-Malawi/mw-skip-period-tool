package mw.gov.health.lmis.migration.tool.scm.repository;

import static mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.OpenLmisNumberUtils.isNotZero;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Repository
public class AdjustmentAccessRepository extends BaseAccessRepository<Adjustment> {
  private Map<Integer, ItemDetails> table = Maps.newHashMap();

  /**
   * Prepare data that would speed up search process.
   */
  public void init() {
    Database database = getDatabase();

    try {
      Cursor cursor = getCursor(database);
      Row row;

      int rows = 0;
      while ((row = cursor.getNextRow()) != null) {
        Adjustment element = mapRow(row);

        if (!isNotZero(element.getQuantity())) {
          continue;
        }

        ItemDetails details = table.get(element.getItem());

        if (null == details) {
          details = new ItemDetails();
          details.first = rows;
          details.count = 1;

          table.put(element.getItem(), details);
        } else {
          details.count += 1;
        }

        ++rows;
      }
    } catch (IOException exp) {
      throw new IllegalStateException("Can't retrieve data for table: " + getTableName(), exp);
    } finally {
      IOUtils.closeQuietly(database);
    }
  }

  /**
   * Finds all adjustments for the given item.
   */
  public Map<Integer, List<Adjustment>> search(List<Integer> itemIds) {
    Map<Integer, List<Adjustment>> results = Maps.newHashMap();

    for (int i = 0, size = itemIds.size(); i < size; ++i) {
      Integer id = itemIds.get(i);
      ItemDetails details = table.get(id);

      List<Adjustment> adjustments;

      if (null == details) {
        adjustments = Lists.newArrayList();
      } else {
        adjustments = findAll(
            elem -> isNotZero(elem.getQuantity()) && id.equals(elem.getItem()),
            details.first,
            details.count
        );
      }

      results.put(id, adjustments);
    }

    return results;
  }

  @Override
  String getTableName() {
    return properties.getTableNames().getAdjustment();
  }

  @Override
  Adjustment mapRow(Row row) {
    return new Adjustment(row);
  }

  public static final class ItemDetails {
    private int first;
    private int count;
  }
}
