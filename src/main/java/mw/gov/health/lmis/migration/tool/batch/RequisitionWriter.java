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
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;

import java.util.Collection;
import java.util.List;

@Component
public class RequisitionWriter implements ItemWriter<List<Requisition>> {

  @Autowired
  private OlmisRequisitionRepository olmisRequisitionRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OlmisProgramRepository olmisProgramRepository;

  @Autowired
  private OlmisUserRepository olmisUserRepository;

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
          olmisRequisitionRepository.save(requisition);

          if (requisition.getStatus() == ExternalStatus.RELEASED) {
            createOrder(requisition);
          }
        });
  }

  private void createOrder(Requisition requisition) {
    Program program = olmisProgramRepository.findOne(requisition.getProgramId());

    String username = toolProperties.getParameters().getCreator();
    User user = olmisUserRepository.findByUsername(username);

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
