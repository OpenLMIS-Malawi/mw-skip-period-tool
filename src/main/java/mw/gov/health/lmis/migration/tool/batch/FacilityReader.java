package mw.gov.health.lmis.migration.tool.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.scm.domain.Facility;
import mw.gov.health.lmis.migration.tool.scm.repository.FacilityAccessRepository;

import java.util.LinkedList;

@Component
public class FacilityReader implements ItemReader<String> {
  private static LinkedList<Facility> facilities = null;

  @Autowired
  private FacilityAccessRepository facilityRepository;

  @Override
  public synchronized String read() {
    synchronized (FacilityReader.class) {
      if (null == facilities) {
        facilities = new LinkedList<>(facilityRepository.findAll());
      }

      Facility facility = facilities.pollFirst();
      return null == facility ? null : facility.getCode();
    }
  }

}
