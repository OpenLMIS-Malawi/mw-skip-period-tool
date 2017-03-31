package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryProductJoin {
  private Integer id;
  private Integer program;
  private String product;
  private Integer order;
  private Integer deOrder;
}
