package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Main {
  private String facility;
  private LocalDateTime processingDate;
  private LocalDateTime createdDate;
  private String createdBy;
  private LocalDateTime modifiedDate;
  private String modifiedBy;
  private Short entered;
  private String notes;
  private Integer noStockVisits;
  private LocalDateTime receivedDate;
  private LocalDateTime shipmentReceivedData;
  private Integer newCases;
  private Integer retreatmentCases;
  private Integer pediatricCases;
  private Integer logEntry;
  private LocalDateTime preparedDate;
}
