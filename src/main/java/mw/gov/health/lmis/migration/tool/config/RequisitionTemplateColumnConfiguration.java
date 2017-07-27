package mw.gov.health.lmis.migration.tool.config;

import lombok.Getter;
import lombok.Setter;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.SourceType;

@Getter
@Setter
public class RequisitionTemplateColumnConfiguration {
  private String name;
  private String label;
  private SourceType source;
  private Boolean displayed;
  private String definition;
  private Integer displayOrder;
}
