package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class Item extends BaseEntity {

  public Item(Row row) {
    super(row);
  }

  public Integer getId() {
    return getInt("ci_ctrItemID");
  }

  public String getFacility() {
    return getString("Fac_Code");
  }

  public Date getProcessingDate() {
    return getDate("P_Date");
  }

  public String getProductName() {
    return getString("Prod_Name");
  }

  public String getProductDose() {
    return getString("Prod_Dose");
  }

  public Integer getCategoryProduct() {
    return getInt("lngCatProductID");
  }

  public Integer getProduct() {
    return getInt("lngProductID");
  }

  public Integer getOpeningBalance() {
    return getInt("Open_Bal");
  }

  public Integer getReceipts() {
    return getInt("Receipts");
  }

  public Integer getIssuedQuantity() {
    return getInt("Issues");
  }

  public Integer getDispensedQuantity() {
    return getInt("Dispensed");
  }

  public Integer getAdjustmentCount() {
    return getInt("Adjustments");
  }

  public String getAdjustmentType() {
    return getString("Adj_Type");
  }

  public Integer getClosingBalance() {
    return getInt("Closing_Bal");
  }

  public Integer getSixMoBalance() {
    return getInt("6MO_Balance");
  }

  public Boolean getErrorOpeningBalance() {
    return getBoolean("Err_OB");
  }

  public Boolean getErrorClosingBalance() {
    return getBoolean("Err_CB");
  }

  public Short getDataEntryStatus() {
    return getShort("DE_Stat");
  }

  public Integer getNewVisits() {
    return getInt("New_Visits");
  }

  public Integer getContinuingVisits() {
    return getInt("Cont_Visits");
  }

  public Boolean getErrorRequiredQuantity() {
    return getBoolean("Err_qty_rqrd");
  }

  public Boolean getErrorReceivedQuantity() {
    return getBoolean("Err_qty_rcvd");
  }

  public Boolean getErrorAverageMonthlyConsumption() {
    return getBoolean("Err_AMC");
  }

  public Integer getAverageMonthlyConsumption() {
    return getInt("Avg_mnthly_cons");
  }

  public Integer getRequiredQuantity() {
    return getInt("Qty_required");
  }

  public Integer getReceivedQuantity() {
    return getInt("Qty_received");
  }

  public String getNote() {
    return getString("txtNotes");
  }

  public Integer getOnOrderQuantity() {
    return getInt("Qty_OnOrder");
  }

  public Boolean getErrorDispensedQuantityVsPurpose() {
    return getBoolean("Err_qty_DispVsPurpose");
  }

  public Integer getOnHandQuantity() {
    return getInt("Qty_OnHand");
  }

  public Boolean getProductStockedOut() {
    return getBoolean("fStockedOut");
  }

  public Integer getCalculatedRequiredQuantity() {
    return getInt("Qty_RequiredCalc");
  }

  public Short getStockedOutDays() {
    return getShort("intStockedOutDays");
  }

  public Integer getAdjustedDispensedQuantity() {
    return getInt("DispensedAdj");
  }

  public Integer getSupplierIssuedQuantity() {
    return getInt("qtySupplierIssued");
  }

  public Integer getMaxStockQuantity() {
    return getInt("lngQtyMaxStock");
  }

  public Boolean getErrorMaxStockQuantity() {
    return getBoolean("ERR_MaxStock");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    Item that = (Item) obj;

    return new EqualsBuilder()
        .append(getProduct(), that.getProduct())
        .append(getReceipts(), that.getReceipts())
        .append(getDispensedQuantity(), that.getDispensedQuantity())
        .append(getStockedOutDays(), that.getStockedOutDays())
        .append(getClosingBalance(), that.getClosingBalance())
        .append(getCalculatedRequiredQuantity(), that.getCalculatedRequiredQuantity())
        .append(getRequiredQuantity(), that.getRequiredQuantity())
        .append(getAdjustedDispensedQuantity(), that.getAdjustedDispensedQuantity())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(getProduct())
        .append(getReceipts())
        .append(getDispensedQuantity())
        .append(getStockedOutDays())
        .append(getClosingBalance())
        .append(getCalculatedRequiredQuantity())
        .append(getRequiredQuantity())
        .append(getAdjustedDispensedQuantity())
        .toHashCode();
  }
}
