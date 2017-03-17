package org.openlmis.migration.tool.batch;

import org.openlmis.migration.tool.domain.Main;
import org.openlmis.migration.tool.repository.MainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MainReader extends AbstractPagingItemReader<Main> {
  private static final Logger LOG = LoggerFactory.getLogger(MainReader.class);

  @Autowired
  private MainRepository mainRepository;

  public MainReader() {
    setPageSize(1);
  }

  @Override
  protected void doReadPage() {
    LOG.debug("Reading mains. Page: {}, page size: {}", getPage(), getPageSize());

    Page<Main> mains = mainRepository.findAll(new PageRequest(getPage(), getPageSize()));
    List<Main> content = mains.getContent();

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
