package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.ExternalStatus;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.Order;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderNumberConfiguration;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.OrderStatus;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.domain.ProofOfDelivery;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.repository.OrderRepository;
import mw.gov.health.lmis.migration.tool.openlmis.fulfillment.repository.ProofOfDeliveryRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;

import java.util.List;

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
  private ToolProperties toolProperties;

  @Autowired
  private AppBatchContext context;

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

        requisitionRepository.save(requisition);

        if (requisition.getStatus() == ExternalStatus.RELEASED) {
          createOrder(requisition, program);
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

}
