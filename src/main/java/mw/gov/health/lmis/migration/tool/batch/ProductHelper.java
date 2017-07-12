package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionLineItem;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

@Component
public class ProductHelper {
  private static final Map<Signature, Integer> CLOSING_BALANCES = new WeakHashMap<>();
  private static final Set<Signature> CHECKED_PREVIOUS_REQUISITIONS = Collections
      .newSetFromMap(new WeakHashMap<>());

  @Autowired
  private RequisitionRepository requisitionRepository;

  @Autowired
  private AppBatchContext context;

  void add(Requisition requisition) {
    for (int i = 0, size = requisition.getRequisitionLineItems().size(); i < size; ++i) {
      RequisitionLineItem line = requisition.getRequisitionLineItems().get(i);
      Signature signature = new Signature(requisition, line);

      CLOSING_BALANCES.put(signature, line.getStockOnHand());
    }
  }

  Integer getClosingBalance(Requisition requisition, UUID product) {
    ProcessingPeriod previousPeriod = context
        .findPreviousPeriod(requisition.getProcessingPeriodId());

    if (null == previousPeriod) {
      return 0;
    }

    Signature signature = new Signature(requisition, previousPeriod.getId(), product);
    boolean checked = CHECKED_PREVIOUS_REQUISITIONS.contains(signature);

    return getClosingBalance(signature, checked);
  }

  private Integer getClosingBalance(Signature signature, boolean checked) {
    if (CLOSING_BALANCES.containsKey(signature)) {
      return Optional.ofNullable(CLOSING_BALANCES.get(signature)).orElse(0);
    } else if (checked) {
      // in this case the previous requisition does not contain the given product.
      return CLOSING_BALANCES.computeIfAbsent(signature, key -> 0);
    } else {
      Requisition previousRequisition = getPreviousRequisition(signature);

      if (null != previousRequisition) {
        add(previousRequisition);
      }

      CHECKED_PREVIOUS_REQUISITIONS.add(signature);

      return getClosingBalance(signature, true);
    }
  }

  private Requisition getPreviousRequisition(Signature signature) {
    List<Requisition> requisitionsByPeriod = requisitionRepository
        .findByFacilityIdAndProgramIdAndProcessingPeriodId(
            signature.getFacility(), signature.getProgram(), signature.getPeriod()
        );

    return requisitionsByPeriod.isEmpty() ? null : requisitionsByPeriod.get(0);
  }

  @EqualsAndHashCode
  @Getter
  private static final class Signature {
    private final UUID facility;
    private final UUID program;
    private final UUID period;
    private final UUID product;

    Signature(Requisition requisition, RequisitionLineItem line) {
      this(requisition, requisition.getProcessingPeriodId(), line.getOrderableId());
    }

    Signature(Requisition requisition, UUID period, UUID product) {
      this.facility = requisition.getFacilityId();
      this.program = requisition.getProgramId();
      this.period = period;
      this.product = product;
    }

  }
}
