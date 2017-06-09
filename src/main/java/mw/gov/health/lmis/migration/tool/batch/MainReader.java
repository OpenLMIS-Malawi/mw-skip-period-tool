package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.repository.MainRepository;

import java.util.TreeSet;

@Component
public class MainReader implements ItemReader<Main> {
  private static TreeSet<Main> mains = null;

  @Autowired
  private MainRepository mainRepository;

  @Override
  public synchronized Main read() {
    synchronized (MainReader.class) {
      if (null == mains) {
        mains = new TreeSet<>(mainRepository.searchInPeriod());
      }

      return mains.pollFirst();
    }
  }
}
