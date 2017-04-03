package mw.gov.health.lmis.migration.tool.scm.domain;

import com.healthmarketscience.jackcess.Row;

import java.util.Date;
import java.util.Objects;

public class Main extends BaseEntity implements Comparable<Main> {

  public Main(Row row) {
    super(row);
  }

  public String getFacility() {
    return getString("Fac_Code");
  }

  public Date getProcessingDate() {
    return getDate("P_Date");
  }

  public Date getCreatedDate() {
    return getDate("DE_Date");
  }

  public String getCreatedBy() {
    return getString("DE_Staff");
  }

  public Date getModifiedDate() {
    return getDate("LC_Date");
  }

  public String getModifiedBy() {
    return getString("LC_Staff");
  }

  public Short getEntered() {
    return getShort("CTF_Stat");
  }

  public String getNotes() {
    return getString("CTF_Comment");
  }

  public Integer getNoStockVisits() {
    return getInt("NoStock_Visits");
  }

  public Date getReceivedDate() {
    return getDate("dtmReceived");
  }

  public Date getShipmentReceivedData() {
    return getDate("dtmShipment");
  }

  public Integer getNewCases() {
    return getInt("ctf_lngCasesNew");
  }

  public Integer getRetreatmentCases() {
    return getInt("ctf_lngCasesRetreatments");
  }

  public Integer getPediatricCases() {
    return getInt("ctf_lngCasesPediatric");
  }

  public Integer getLogEntry() {
    return getInt("dtl_lngID");
  }

  public Date getPreparedDate() {
    return getDate("dtmPrepared");
  }


  @Override
  public int hashCode() {
    return this.getRowId().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Main) {
      Main that = (Main) obj;

      return Objects.equals(that.getRowId(), that.getRowId());
    }

    return false;
  }

  @Override
  public int compareTo(Main that) {
    int compare = this.getFacility().compareTo(that.getFacility());

    if (0 != compare) {
      return compare;
    }

    return this.getProcessingDate().compareTo(that.getProcessingDate());
  }
}
