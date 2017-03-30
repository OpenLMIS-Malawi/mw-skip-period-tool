package mw.gov.health.lmis.migration.tool.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.Arguments;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.repository.MainRepository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SupplyManagerExtactor extends AbstractPagingItemReader<Main> {
  private static final Logger LOG = LoggerFactory.getLogger(SupplyManagerExtactor.class);

  @Autowired
  private MainRepository mainRepository;

  @Autowired
  private Arguments arguments;

  public SupplyManagerExtactor() {
    setPageSize(10);
  }

  @Override
  protected void doReadPage() {
    LOG.debug("Reading mains. Page: {}, page size: {}", getPage(), getPageSize());

    List<Main> content = mainRepository.searchInPeriod(
        arguments.getPeriod(), getPage(), getPageSize()
    );

    LOG.info("{} main have been retrieved.", content.size());

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
