package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Item {
  private Integer id;
  private String facility;
  private LocalDateTime processingDate;
  private String productName;
  private String productDose;
  private Integer categoryProduct;
  private Integer product;
  private Integer openingBalance;
  private Integer receipts;
  private Integer issuedQuantity;
  private Integer dispensedQuantity;
  private Integer adjustmentCount;
  private String adjustmentType;
  private Integer closingBalance;
  private Integer sixMoBalance;
  private Boolean errorOpeningBalance;
  private Boolean errorClosingBalance;
  private Short dataEntryStatus;
  private Integer newVisits;
  private Integer continuingVisits;
  private Boolean errorRequiredQuantity;
  private Boolean errorReceivedQuantity;
  private Boolean errorAverageMonthlyConsumption;
  private Integer averageMonthlyConsumption;
  private Integer requiredQuantity;
  private Integer receivedQuantity;
  private String note;
  private Integer onOrderQuantity;
  private Boolean errorDispensedQuantityVsPurpose;
  private Integer onHandQuantity;
  private Boolean productStockedOut;
  private Integer calculatedRequiredQuantity;
  private Short stockedOutDays;
  private Integer adjustedDispensedQuantity;
  private Integer supplierIssuedQuantity;
  private Integer maxStockQuantity;
  private Boolean errorMaxStockQuantity;
}
