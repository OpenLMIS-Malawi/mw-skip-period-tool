package mw.gov.health.lmis.migration.tool.batch;

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
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.User;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.ProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.UserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;

import java.util.Collection;
import java.util.List;

@Component
public class RequisitionWriter implements ItemWriter<List<Requisition>> {

  @Autowired
  private RequisitionRepository requisitionRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProofOfDeliveryRepository proofOfDeliveryRepository;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Writes Requisitions into OpenLMIS database.
   */
  @Override
  public void write(List<? extends List<Requisition>> items) throws Exception {
    items
        .stream()
        .flatMap(Collection::stream)
        .forEach(requisition -> {
          requisitionRepository.save(requisition);

          if (requisition.getStatus() == ExternalStatus.RELEASED) {
            createOrder(requisition);
          }
        });
  }

  private void createOrder(Requisition requisition) {
    Program program = programRepository.findOne(requisition.getProgramId());

    String username = toolProperties.getParameters().getCreator();
    User user = userRepository.findByUsername(username);

    OrderNumberConfiguration config = toolProperties
        .getParameters()
        .getOrderNumberConfiguration();

    Order order = Order.newOrder(requisition, user);
    order.setStatus(OrderStatus.RECEIVED);
    order.setOrderCode(config.generateOrderNumber(order, program));

    order = orderRepository.save(order);

    proofOfDeliveryRepository.save(new ProofOfDelivery(order));
  }

}
