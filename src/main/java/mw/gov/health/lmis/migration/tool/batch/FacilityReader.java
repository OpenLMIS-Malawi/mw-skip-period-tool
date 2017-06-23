package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.MappingHelper;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.Facility;
import mw.gov.health.lmis.migration.tool.scm.repository.FacilityAccessRepository;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class FacilityReader implements ItemReader<String> {
  private static NavigableSet<String> facilities = null;

  @Autowired
  private FacilityAccessRepository facilityRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public synchronized String read() {
    synchronized (FacilityReader.class) {
      if (null == facilities) {
        facilities = facilityRepository
            .findAll()
            .stream()
            .map(Facility::getCode)
            .map(code -> MappingHelper.getFacilityCode(toolProperties, code))
            .collect(Collectors.toCollection(TreeSet::new));
      }

      return facilities.pollFirst();
    }
  }

}
