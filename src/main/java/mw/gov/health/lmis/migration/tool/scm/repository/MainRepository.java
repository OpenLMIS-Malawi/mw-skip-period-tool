package mw.gov.health.lmis.migration.tool.scm.repository;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addMonths;
import static org.apache.commons.lang3.time.DateUtils.addYears;
import static org.apache.commons.lang3.time.DateUtils.truncate;

import com.healthmarketscience.jackcess.Row;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.config.ToolParameters;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MainRepository extends BaseRepository<Main> {
  private Date nowAtStartOfDay;

  /**
   * Creates new instance with passing tool properties.
   */
  @Autowired
  public MainRepository(ToolProperties toolProperties) {
    ToolParameters.Interval period = toolProperties.getParameters().getInterval();

    nowAtStartOfDay = new Date();
    nowAtStartOfDay = addYears(nowAtStartOfDay, -1 * period.getYears());
    nowAtStartOfDay = addMonths(nowAtStartOfDay, -1 * period.getMonths());
    nowAtStartOfDay = addDays(nowAtStartOfDay, -1 * period.getDays());
    nowAtStartOfDay = truncate(nowAtStartOfDay, Calendar.DATE);
  }

  /**
   * Find mains that have processing date in the given period.
   */
  public List<Main> searchInPeriod(long page, long pageSize) {
    List<Main> mains = search(main -> !main.getProcessingDate().before(nowAtStartOfDay));
    return mains
        .stream()
        .skip(page * pageSize)
        .limit(pageSize)
        .collect(Collectors.toList());
  }

  @Override
  String getTableName() {
    return properties.getTableNames().getMain();
  }

  @Override
  Main mapRow(Row row) {
    return new Main(row);
  }
}
