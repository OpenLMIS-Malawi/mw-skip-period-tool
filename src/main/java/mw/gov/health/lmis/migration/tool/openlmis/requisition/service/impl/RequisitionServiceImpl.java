package mw.gov.health.lmis.migration.tool.openlmis.requisition.service.impl;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.migration.tool.config.ToolProgramWarehouseMapping;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicZone;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.FacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.StatusMessage;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.service.RequisitionService;

import java.util.List;

@Service
public class RequisitionServiceImpl implements RequisitionService {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequisitionServiceImpl.class);

  @Autowired
  private RequisitionRepository requisitionRepository;

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public void addStatusMessage(Requisition requisition, User user, String generalNote) {
    if (isBlank(generalNote)) {
      return;
    }

    if (length(generalNote) > 255) {
      LOGGER.warn("The general note ({}) is too long. Skipping...", generalNote);
      return;
    }

    StatusMessage statusMessage = StatusMessage.newStatusMessage(
        requisition, user.getId(), user.getFirstName(), user.getLastName(), generalNote,
        ExternalStatus.AUTHORIZED
    );

    requisition.setStatusMessages(Lists.newArrayList(statusMessage));
  }

  @Override
  public void convertToOrder(Requisition requisition, User user, Program program,
                             Facility facility) {
    Facility warehouse = null;
    List<ToolProgramWarehouseMapping> warehouses = toolProperties
        .getMapping()
        .getPrograms()
        .stream()
        .filter(mp -> mp.getCode().equals(program.getCode().toString()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException(
                "Can't find warehouse mapping for program: " + program.getCode()
            )
        )
        .getWarehouses();

    GeographicZone zone = facility.getGeographicZone();

    while (null != zone) {
      String zoneName = zone.getName();

      for (ToolProgramWarehouseMapping supplying : warehouses) {
        if (null == supplying.getGeographicZone()
            || containsIgnoreCase(zoneName, supplying.getGeographicZone())) {
          warehouse = facilityRepository.findByCode(supplying.getCode());
          break;
        }
      }

      if (null == warehouse) {
        zone = zone.getParent();
      } else {
        break;
      }
    }

    if (null != warehouse) {
      requisition.setSupplyingFacilityId(warehouse.getId());
    } else {
      throw new IllegalStateException(
          "can't find supplying facility for program: "
              + program.getName()
              + " and facility: "
              + facility.getName()
      );
    }

    requisition.release(user.getId());
  }

}
