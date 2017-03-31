package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import mw.gov.health.lmis.migration.tool.scm.domain.Adjustment;
import mw.gov.health.lmis.migration.tool.scm.domain.AdjustmentType;
import mw.gov.health.lmis.migration.tool.scm.domain.CategoryProductJoin;
import mw.gov.health.lmis.migration.tool.scm.domain.Comment;
import mw.gov.health.lmis.migration.tool.scm.domain.Facility;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.domain.Product;
import mw.gov.health.lmis.migration.tool.scm.domain.Program;
import mw.gov.health.lmis.migration.tool.scm.domain.SystemDefault;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SuppressWarnings("PMD.TooManyMethods")
final class RowMapper {

  private RowMapper() {
    throw new UnsupportedOperationException();
  }

  static Adjustment adjustment(Row row) {
    Adjustment adjustment = new Adjustment();
    adjustment.setId(row.getInt("cta_lngID"));
    adjustment.setItem(row.getInt("ctf_ItemID"));
    adjustment.setType(row.getString("Type_Code"));
    adjustment.setQuantity(row.getInt("cta_lngQty"));

    return adjustment;
  }

  static AdjustmentType adjustmentType(Row row) {
    AdjustmentType type = new AdjustmentType();
    type.setCode(row.getString("Type_Code"));
    type.setName(row.getString("Type_Name"));
    type.setNegative(row.getBoolean("Always_Negative"));
    type.setUserCanEnter(row.getBoolean("UserCanEnter"));
    type.setActive(row.getBoolean("Adj_Active"));
    type.setId(row.getString("adj_guidId"));

    return type;
  }

  static CategoryProductJoin categoryProductJoin(Row row) {
    CategoryProductJoin category = new CategoryProductJoin();
    category.setId(row.getInt("ID"));
    category.setProgram(row.getInt("lngCategoryID"));
    category.setProduct(row.getString("strProductID"));
    category.setOrder(row.getInt("intOrder"));
    category.setDeOrder(row.getInt("intDEOrder"));

    return category;
  }

  static Facility facility(Row row) {
    Facility facility = new Facility();
    facility.setCode(row.getString("Fac_Code"));
    facility.setName(row.getString("Fac_Name"));
    facility.setType(row.getString("Fac_Type"));
    facility.setSupplyingCode(row.getString("Sup_Code"));
    facility.setProgram(row.getString("Program"));
    facility.setContact(row.getString("Fac_Contact"));
    facility.setAddress1(row.getString("Fac_Address1"));
    facility.setAddress2(row.getString("Fac_Address2"));
    facility.setCity(row.getString("Fac_City"));
    facility.setState(row.getString("Fac_State"));
    facility.setRescode(row.getString("Fac_Rescode"));
    facility.setPhone(row.getString("Fac_Phone"));
    facility.setFax(row.getString("Fac_FAX"));
    facility.setDispense(row.getBoolean("Field_Dispense"));
    facility.setMaxMonthsOfSupply(row.getDouble("MaxMOS"));
    facility.setMinMonthsOfSupply(row.getDouble("MinMOS"));
    facility.setActive(row.getBoolean("Active"));
    facility.setWarehouse(row.getBoolean("Warehouse"));
    facility.setHighestLevel(row.getBoolean("Highest_LV"));
    facility.setRole(row.getInt("Distribution_Role"));
    facility.setDataCenter(row.getString("dc_GuidID"));
    facility.setId(row.getString("fac_guidId"));

    return facility;
  }

  static Item item(Row row) {
    Item item = new Item();
    item.setId(row.getInt("ci_ctrItemID"));
    item.setFacility(row.getString("Fac_Code"));
    item.setProcessingDate(convert(row.getDate("P_Date")));
    item.setProductName(row.getString("Prod_Name"));
    item.setProductDose(row.getString("Prod_Dose"));
    item.setCategoryProduct(row.getInt("lngCatProductID"));
    item.setProduct(row.getInt("lngProductID"));
    item.setOpeningBalance(row.getInt("Open_Bal"));
    item.setReceipts(row.getInt("Receipts"));
    item.setIssuedQuantity(row.getInt("Issues"));
    item.setDispensedQuantity(row.getInt("Dispensed"));
    item.setAdjustmentCount(row.getInt("Adjustments"));
    item.setAdjustmentType(row.getString("Adj_Type"));
    item.setClosingBalance(row.getInt("Closing_Bal"));
    item.setSixMoBalance(row.getInt("6MO_Balance"));
    item.setErrorOpeningBalance(row.getBoolean("Err_OB"));
    item.setErrorClosingBalance(row.getBoolean("Err_CB"));
    item.setDataEntryStatus(row.getShort("DE_Stat"));
    item.setNewVisits(row.getInt("New_Visits"));
    item.setContinuingVisits(row.getInt("Cont_Visits"));
    item.setErrorRequiredQuantity(row.getBoolean("Err_qty_rqrd"));
    item.setErrorReceivedQuantity(row.getBoolean("Err_qty_rcvd"));
    item.setErrorAverageMonthlyConsumption(row.getBoolean("Err_AMC"));
    item.setAverageMonthlyConsumption(row.getInt("Avg_mnthly_cons"));
    item.setRequiredQuantity(row.getInt("Qty_required"));
    item.setReceivedQuantity(row.getInt("Qty_received"));
    item.setNote(row.getString("txtNotes"));
    item.setOnOrderQuantity(row.getInt("Qty_OnOrder"));
    item.setErrorDispensedQuantityVsPurpose(row.getBoolean("Err_qty_DispVsPurpose"));
    item.setOnHandQuantity(row.getInt("Qty_OnHand"));
    item.setProductStockedOut(row.getBoolean("fStockedOut"));
    item.setCalculatedRequiredQuantity(row.getInt("Qty_RequiredCalc"));
    item.setStockedOutDays(row.getShort("intStockedOutDays"));
    item.setAdjustedDispensedQuantity(row.getInt("DispensedAdj"));
    item.setSupplierIssuedQuantity(row.getInt("qtySupplierIssued"));
    item.setMaxStockQuantity(row.getInt("lngQtyMaxStock"));
    item.setErrorMaxStockQuantity(row.getBoolean("ERR_MaxStock"));

    return item;
  }

  static Main main(Row row) {
    Main main = new Main();
    main.setFacility(row.getString("Fac_Code"));
    main.setProcessingDate(convert(row.getDate("P_Date")));
    main.setCreatedDate(convert(row.getDate("DE_Date")));
    main.setCreatedBy(row.getString("DE_Staff"));
    main.setModifiedDate(convert(row.getDate("LC_Date")));
    main.setModifiedBy(row.getString("LC_Staff"));
    main.setEntered(row.getShort("CTF_Stat"));
    main.setNotes(row.getString("CTF_Comment"));
    main.setNoStockVisits(row.getInt("NoStock_Visits"));
    main.setReceivedDate(convert(row.getDate("dtmReceived")));
    main.setShipmentReceivedData(convert(row.getDate("dtmShipment")));
    main.setNewCases(row.getInt("ctf_lngCasesNew"));
    main.setRetreatmentCases(row.getInt("ctf_lngCasesRetreatments"));
    main.setPediatricCases(row.getInt("ctf_lngCasesPediatric"));
    main.setLogEntry(row.getInt("dtl_lngID"));
    main.setPreparedDate(convert(row.getDate("dtmPrepared")));

    return main;
  }

  static Product product(Row row) {
    Product product = new Product();
    product.setProductId(row.getString("strProductID"));
    product.setName(row.getString("Prod_Name"));
    product.setDose(row.getString("Prod_Dose"));
    product.setMethodAssociated(row.getInt("mtd_lngMethodID"));
    product.setCoupleYearsOfProtection(row.getDouble("Prod_CYP"));
    product.setActive(row.getBoolean("Prod_Is_Active"));
    product.setShipmentQuantityIn(row.getInt("Prod_Ship_Qty_In"));
    product.setShipmentQuantityWarehouse(row.getInt("Prod_Ship_Qty_Whse"));
    product.setShipmentQuantityServiceDeliveryPoint(row.getInt("Prod_Ship_Qty_SDP"));
    product.setShipmentVolumeInbound(row.getFloat("pr_sinVolumeIn"));
    product.setShipmentVolumeWarehouse(row.getFloat("pr_sinVolumeWhse"));
    product.setShipmentVolumeServiceDeliveryPoint(row.getFloat("pr_sinVolumeSDP"));
    product.setId(row.getInt("Pr_lngProductID"));
    product.setCaseLength(row.getFloat("pr_sinCaseLength"));
    product.setCaseWidth(row.getFloat("pr_sinCaseWidth"));
    product.setCaseHeight(row.getFloat("pr_sinCaseHeight"));
    product.setIndicator(row.getBoolean("pr_fIndicator"));
    product.setProductsInCase(row.getInt("pr_lngCaseSize"));
    product.setProgramName(row.getString("Program_Name"));
    product.setDataEntryOrder(row.getShort("DE_Order"));
    product.setNumberOfUnitsPerDay(row.getFloat("pr_intUnitsPerDay"));
    product.setNonFullSupply(row.getBoolean("pr_fNonFullSupply"));
    product.setFormulation(row.getString("Prod_Form"));
    product.setPackSize(row.getDouble("Prod_LowestUnitQty"));
    product.setMeasurementUnit(row.getString("Prod_LowestUnit"));
    product.setDescription(row.getString("Prod_Description"));
    product.setPurposePopup(row.getBoolean("fPOU"));

    return product;
  }

  static Program program(Row row) {
    Program program = new Program();
    program.setName(row.getString("Program_Name"));
    program.setId(row.getInt("Program_ID"));
    program.setOrder(row.getInt("intOrder"));
    program.setParentId(row.getInt("ParentId"));

    return program;
  }

  static SystemDefault systemDefault(Row row) {
    SystemDefault systemDefault = new SystemDefault();
    systemDefault.setSerialNumber(row.getString("Serial_No"));
    systemDefault.setReportTitle1(row.getString("Report_Title_1"));
    systemDefault.setReportTitle2(row.getString("Report_Title_2"));
    systemDefault.setReportTitle3(row.getString("Report_Title_3"));
    systemDefault.setNumberOfPeriodsToAverage(row.getShort("MonthsToSample"));
    systemDefault.setExportPath(row.getString("Export_Path"));
    systemDefault.setCurrentMonth(convert(row.getDate("CurrentMonth")));
    systemDefault.setVersion(row.getString("Db_Version"));
    systemDefault.setQuarterly(row.getBoolean("blnQuarterly"));
    systemDefault.setReportingPeriod(row.getShort("dp_intMonths"));
    systemDefault.setDrpPortionLogistics2000(row.getBoolean("sys_fUseDRP"));
    systemDefault.setLmisPortionLogistics2000(row.getBoolean("sys_fUseLMIS"));
    systemDefault.setCalculationLevel(row.getInt("sys_intCalcType"));
    systemDefault.setMeasureUnit(row.getString("sys_strMeasure"));
    systemDefault.setMainFacility(row.getString("sys_strDRPMain"));
    systemDefault.setLastReportNumberOfMonths(row.getInt("sys_intLastRepot"));
    systemDefault.setLanguage(row.getShort("sys_intLanguage"));
    systemDefault.setUseServiceStastics(row.getBoolean("sys_fUseServiceStatistics"));
    systemDefault.setUseQuantityOnOrder(row.getBoolean("sys_fUseQtyOnOrder"));
    systemDefault.setUseNewUsersOnly(row.getBoolean("sys_fUseNewUsersOnly"));
    systemDefault.setUsePurposeOfUse(row.getBoolean("sys_fUsePurposeofUse"));
    systemDefault.setUseQuantityOnHand(row.getBoolean("sys_fUseQtyOnHand"));
    systemDefault.setUseCategoryLevelMinMax(row.getBoolean("sys_fUseCatMinMax"));
    systemDefault.setUseOverrideZeroQuantity(row.getBoolean("sys_fUseOverrideRQD"));
    systemDefault.setTrackingFacilities(row.getShort("sys_intTracking"));
    systemDefault.setStartReportingDate(convert(row.getDate("sys_dtmStart")));
    systemDefault.setMultiTierRole(row.getShort("Sys_intMultiTierRole"));
    systemDefault.setUseTurnOnMtChangeLogging(row.getBoolean("sys_fUseTrackMTChangeLog"));
    systemDefault.setDefaultFacilityMin(row.getDouble("sys_dblDefaultFacilityMin"));
    systemDefault.setDefaultFacilityMax(row.getDouble("sys_dblDefaultFacilityMax"));
    systemDefault.setMultipleComments(row.getDouble("sys_fUseTrackMultiComments"));
    systemDefault.setMultipleAdjustments(row.getDouble("sys_fUseTrackMultiAdj"));

    return systemDefault;
  }

  static Comment comment(Row row) {
    Comment comment = new Comment();
    comment.setId(row.getInt("ctfc_lngID"));
    comment.setItem(row.getInt("ctf_ItemID"));
    comment.setType(row.getString("Com_Code"));
    comment.setComment(row.getString("ctfc_txt"));

    return comment;
  }

  private static LocalDateTime convert(Date date) {
    return null == date ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

}
