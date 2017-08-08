package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.NavigableSet;
import java.util.TreeSet;

@Component
public class FacilityReader implements ItemReader<String> {
  private static NavigableSet<String> facilities = null;

  @Override
  public synchronized String read() {
    synchronized (FacilityReader.class) {
      if (null == facilities) {
        facilities = new TreeSet<>();
      }

      return facilities.pollFirst();
    }
  }

}
