package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Repository
public class MainRepository extends BaseRepository<Main> {

  /**
   * Find mains that have processing date in the given period.
   */
  public List<Main> searchInPeriod(Period period, Integer page, Integer pageSize) {
    List<Main> list = search(main -> {
      LocalDateTime processingDate = main.getProcessingDate();
      return !processingDate.isBefore(LocalDate.now().atStartOfDay().minus(period));
    });

    list.sort((o1, o2) -> {
      int compare = o1.getFacility().compareTo(o2.getFacility());

      if (0 != compare) {
        return compare;
      }

      return o1.getProcessingDate().compareTo(o2.getProcessingDate());
    });

    int fromIndex = (page + 1) * pageSize;
    int toIndex = fromIndex + pageSize + 1;

    if (toIndex > list.size()) {
      toIndex = list.size();
    }

    if (fromIndex > toIndex) {
      fromIndex = toIndex;
    }

    return list.subList(fromIndex, toIndex);
  }

  @Override
  String getTableName() {
    return "CTF_Main";
  }

  @Override
  Main mapRow(Row row) {
    return RowMapper.main(row);
  }
}
