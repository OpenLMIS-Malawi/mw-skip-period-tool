package org.openlmis.migration.tool.service;

import org.openlmis.migration.tool.domain.Adjustment;
import org.openlmis.migration.tool.domain.AdjustmentType;
import org.openlmis.migration.tool.domain.CategoryChain;
import org.openlmis.migration.tool.domain.CategoryProductJoin;
import org.openlmis.migration.tool.domain.Comment;
import org.openlmis.migration.tool.domain.CommentType;
import org.openlmis.migration.tool.domain.DataCenter;
import org.openlmis.migration.tool.domain.DataChangeLog;
import org.openlmis.migration.tool.domain.DataReportingPeriod;
import org.openlmis.migration.tool.domain.DataTransferLog;
import org.openlmis.migration.tool.domain.Delivery;
import org.openlmis.migration.tool.domain.DeliveryCrew;
import org.openlmis.migration.tool.domain.DeliveryProduct;
import org.openlmis.migration.tool.domain.DeliveryStop;
import org.openlmis.migration.tool.domain.Demand;
import org.openlmis.migration.tool.domain.DemandStatus;
import org.openlmis.migration.tool.domain.DistributionLevel;
import org.openlmis.migration.tool.domain.Facility;
import org.openlmis.migration.tool.domain.FacilityCategoryLevel;
import org.openlmis.migration.tool.domain.FacilityInventory;
import org.openlmis.migration.tool.domain.FacilityInventoryHeader;
import org.openlmis.migration.tool.domain.FacilityType;
import org.openlmis.migration.tool.domain.FacilityTypeProduct;
import org.openlmis.migration.tool.domain.Group;
import org.openlmis.migration.tool.domain.GroupType;
import org.openlmis.migration.tool.domain.Item;
import org.openlmis.migration.tool.domain.Main;
import org.openlmis.migration.tool.domain.MainUser;
import org.openlmis.migration.tool.domain.Method;
import org.openlmis.migration.tool.domain.Period;
import org.openlmis.migration.tool.domain.Personnel;
import org.openlmis.migration.tool.domain.Product;
import org.openlmis.migration.tool.domain.ProductCost;
import org.openlmis.migration.tool.domain.Program;
import org.openlmis.migration.tool.domain.Purpose;
import org.openlmis.migration.tool.domain.PurposeOfUse;
import org.openlmis.migration.tool.domain.ReportingGroup;
import org.openlmis.migration.tool.domain.ReportingGroupChain;
import org.openlmis.migration.tool.domain.ReportingGroupMember;
import org.openlmis.migration.tool.domain.Route;
import org.openlmis.migration.tool.domain.RouteStop;
import org.openlmis.migration.tool.domain.SystemDefault;
import org.openlmis.migration.tool.domain.TableManagement;
import org.openlmis.migration.tool.domain.Truck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

@Service
public class DisplayService {

  @Autowired
  private EntityManager entityManager;

  /**
   * Temporary method that will display on standard error output all data from an access database.
   * It will be removed in future.
   */
  public void print() {
    Class[] classes = new Class[]{
        Adjustment.class, AdjustmentType.class, CategoryChain.class, CategoryProductJoin.class,
        Comment.class, CommentType.class, DataCenter.class, DataChangeLog.class,
        DataReportingPeriod.class, DataTransferLog.class, Delivery.class, DeliveryCrew.class,
        DeliveryProduct.class, DeliveryStop.class, Demand.class, DemandStatus.class,
        DistributionLevel.class, Facility.class, FacilityCategoryLevel.class,
        FacilityInventory.class, FacilityInventoryHeader.class, FacilityType.class,
        FacilityTypeProduct.class, Group.class, GroupType.class, Item.class, Main.class,
        MainUser.class, Method.class, Period.class, Personnel.class, Product.class,
        ProductCost.class, Program.class, Purpose.class, PurposeOfUse.class, ReportingGroup.class,
        ReportingGroupChain.class, ReportingGroupMember.class, Route.class, RouteStop.class,
        SystemDefault.class, TableManagement.class, Truck.class
    };

    System.err.println("=====================================================================");

    for (Class clazz : classes) {
      Entity annotation = (Entity) clazz.getAnnotation(Entity.class);
      System.err.printf("Table: %s (class: %s)%n", annotation.name(), clazz.getCanonicalName());

      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery query = builder.createQuery(clazz);
      query.from(clazz);


      entityManager
          .createQuery(query)
          .getResultList()
          .forEach(System.err::println);

      System.err.println("=====================================================================");
    }
  }

}
