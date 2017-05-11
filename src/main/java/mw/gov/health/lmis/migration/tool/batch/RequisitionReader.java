package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.Requisition;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionRepository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RequisitionReader extends AbstractPagingItemReader<Requisition> {
  private static final Logger LOG = LoggerFactory.getLogger(SupplyManagerExtractor.class);

  @Autowired
  private OlmisRequisitionRepository olmisRequisitionRepository;

  /**
   * Creates new instance with passed values.
   */
  @Autowired
  public RequisitionReader(OlmisRequisitionRepository olmisRequisitionRepository,
                                ToolProperties toolProperties) {
    this.olmisRequisitionRepository = olmisRequisitionRepository;

    setPageSize(toolProperties.getConfiguration().getBatch().getChunk());
    setSaveState(false);
  }

  @Override
  protected void doReadPage() {
    LOG.debug("Reading requisitions. Page: {}, page size: {}", getPage(), getPageSize());

    Page<Requisition> requisitions = olmisRequisitionRepository
        .findAll(new PageRequest(getPage(), getPageSize()));
    List<Requisition> content = requisitions.getContent();

    LOG.debug("{} requisitions have been retrieved.", content.size());

    // results come from parent - write to them
    if (results == null) {
      // this list impl is used by batch paging readers
      // makes sense since we read only once per page
      results = new CopyOnWriteArrayList<>(content);
    } else {
      results.clear();
      results.addAll(content);
    }
  }

  @Override
  protected void doJumpToPage(int itemIndex) {
    // we don't need to do anything special here
  }

}
