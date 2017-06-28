package mw.gov.health.lmis.migration.tool.config;

public final class MappingHelper {

  private MappingHelper() {
    throw new UnsupportedOperationException();
  }

  /**
   * Retrieve correct adjustment name from mapping section. If there is no key-value pair,
   * a default name will be returned.
   */
  public static String getAdjustmentName(ToolProperties toolProperties, String defaultName) {
    return toolProperties
        .getMapping()
        .getStockAdjustmentReasons()
        .getProperty(defaultName, defaultName);
  }

  /**
   * Retrieve correct facility code from mapping section. If there is no key-value pair,
   * a default code will be returned.
   */
  public static String getFacilityCode(ToolProperties toolProperties, String defaultCode) {
    return toolProperties
        .getMapping()
        .getFacilities()
        .getOrDefault(defaultCode, defaultCode);
  }

  /**
   * Retrieve correct product code from mapping section. If there is no key-value pair,
   * a default code will be returned.
   */
  public static String getProductCode(ToolProperties toolProperties, String name, String code) {
    String property = toolProperties
        .getMapping()
        .getProducts()
        .getProperty(name);

    if (null == property) {
      property = toolProperties
          .getMapping()
          .getProducts()
          .getProperty(code);
    }

    if (null == property) {
      property = code;
    }

    return property;
  }

}
