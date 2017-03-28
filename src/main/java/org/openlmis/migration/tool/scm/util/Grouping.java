package org.openlmis.migration.tool.scm.util;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.function.Function;

@SuppressWarnings("PMD.CyclomaticComplexity")
public final class Grouping {

  private Grouping() {
    throw new UnsupportedOperationException();
  }

  /**
   * Groups a list of items to correct programs. The key in the map is equal to program code.
   */
  public static <T> Multimap<String, T> groupByCategoryName(Iterable<T> items,
                                                      Function<? super T, String> nameExtractor) {
    Multimap<String, T> map = HashMultimap.create();

    for (T item : items) {
      String category = nameExtractor.apply(item);

      if (containsIgnoreCase(category, "Tablets/Capsules")
          || containsIgnoreCase(category, "Injectables")
          || containsIgnoreCase(category, "Vaccines & Toxoids")
          || containsIgnoreCase(category, "Galenicals: Syrups/Elixirs/Suspensions")
          || containsIgnoreCase(category, "Dispensary Items")
          || containsIgnoreCase(category, "Surgical Dressings")
          || containsIgnoreCase(category, "Sutures")
          || containsIgnoreCase(category, "Surgical & Hospital Equipment")
          || containsIgnoreCase(category, "X-Ray Films & Supplies")
          || containsIgnoreCase(category, "Dental Items")
          || containsIgnoreCase(category, "Class D: Raw Materials")
          || containsIgnoreCase(category, "Class M: Laboratory Reagents And Materials (Supplies)")
          || containsIgnoreCase(category, "Class Q: Miscellaneous Items")
          || containsIgnoreCase(category, "Class R: Hospital Bedings")
          || containsIgnoreCase(category, "Class S: Slow Moving Items")
          || containsIgnoreCase(category, "Mental Special")
          || containsIgnoreCase(category, "Surgical Equipment")) {
        map.put("em", item);
      } else if (containsIgnoreCase(category, "Malaria Program Medicines & Health Supplies")) {
        map.put("mal", item);
      } else if (containsIgnoreCase(category, "Reproductive Health Program Medicines")) {
        map.put("fp", item);
      } else if (containsIgnoreCase(category, "HIV/AID Control Program Medicines")) {
        map.put("hiv", item);
      } else if (containsIgnoreCase(category, "Tuberculosis Program Medicines")) {
        map.put("tb", item);
      } else {
        throw new IllegalStateException("Unknown category: " + category);
      }
    }

    return map;
  }

}
