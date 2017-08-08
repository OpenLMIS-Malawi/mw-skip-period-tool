package mw.gov.health.lmis.skip.period.tool.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.skip.period.tool.config.ToolProperties;

import java.util.NavigableSet;
import java.util.TreeSet;

@Component
public class FacilityReader implements ItemReader<String> {
  private static NavigableSet<String> facilities = null;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public synchronized String read() {
    synchronized (FacilityReader.class) {
      if (null == facilities) {
        facilities = new TreeSet<>(toolProperties.getParameters().getFacilities());
      }

      return facilities.pollFirst();
    }
  }

}
