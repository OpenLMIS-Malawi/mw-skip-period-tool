package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolTableNames {
  private String adjustment = "CTF_Adjustments";
  private String adjustmentType = "Adj_Type";
  private String categoryProductJoin = "tblCategoryProductJoin";
  private String comment = "CTF_Comments";
  private String facility = "Facility";
  private String item = "CTF_Item";
  private String main = "CTF_Main";
  private String product = "Product";
  private String program = "Program";
}
