package mw.gov.health.lmis.migration.tool.batch;

import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.Order;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderNumberConfiguration;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderStatus;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.ProofOfDelivery;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.repository.OrderRepository;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.repository.ProofOfDeliveryRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.FacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class RequisitionWriter implements ItemWriter<List<Requisition>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequisitionWriter.class);

  @Autowired
  private RequisitionRepository requisitionRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProofOfDeliveryRepository proofOfDeliveryRepository;

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Autowired
  private AppBatchContext context;

  private Set<Signature> signatures = Sets.newConcurrentHashSet();

  /**
   * Writes Requisitions into OpenLMIS database.
   */
  @Override
  public synchronized void write(List<? extends List<Requisition>> items) throws Exception {
    for (int i = 0, length = items.size(); i < length; ++i) {
      List<Requisition> list = items.get(i);

      for (int j = 0, size = list.size(); j < size; ++j) {
        Requisition requisition = list.get(j);
        Program program = context.findProgramById(requisition.getProgramId());

        if (signatures.add(new Signature(requisition))) {
          requisitionRepository.save(requisition);

          if (requisition.getStatus() == ExternalStatus.RELEASED) {
            createOrder(requisition, program);
          }
        } else {
          Facility facility = facilityRepository.findOne(requisition.getFacilityId());
          ProcessingPeriod period = context.findPeriodById(requisition.getProcessingPeriodId());

          LOGGER.warn(
              "Requisition for facility {}, program {} and period {} exists. Skipping...",
              facility.getCode(), program.getCode(), period.getName()
          );
        }
      }
    }
  }

  private void createOrder(Requisition requisition, Program program) {
    if (null == program) {
      LOGGER.error("Can't find program with id {}", requisition.getProgramId());
    }

    OrderNumberConfiguration config = toolProperties
        .getParameters()
        .getOrderNumberConfiguration();

    Order order = Order.newOrder(requisition, context.getUser());
    order.setStatus(OrderStatus.RECEIVED);
    order.setOrderCode(config.generateOrderNumber(order, program));

    order = orderRepository.save(order);

    proofOfDeliveryRepository.save(new ProofOfDelivery(order));
  }

  @EqualsAndHashCode
  private static final class Signature {
    private final UUID facility;
    private final UUID program;
    private final UUID period;

    Signature(Requisition requisition) {
      this.facility = requisition.getFacilityId();
      this.program = requisition.getProgramId();
      this.period = requisition.getProcessingPeriodId();
    }
  }

}
