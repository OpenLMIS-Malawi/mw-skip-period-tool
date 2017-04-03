package mw.gov.health.lmis.migration.tool.scm.repository;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addMonths;
import static org.apache.commons.lang3.time.DateUtils.addYears;
import static org.apache.commons.lang3.time.DateUtils.truncate;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Row;

import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.config.ToolParameters;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
public class MainRepository extends BaseRepository<Main> {

  /**
   * Find mains that have processing date in the given period.
   */
  public List<Main> searchInPeriod(ToolParameters.Interval period, long page, long pageSize) {
    Date nowAtStartOfDay = truncate(
        addDays(
            addMonths(
                addYears(
                    new Date(),
                    -1 * period.getYears()
                ),
                -1 * period.getMonths()
            ),
            -1 * period.getDays()
        ),
        Calendar.DATE
    );

    Cursor cursor = getCursor();
    TreeSet<Main> set = new TreeSet<>();

    try {
      Row row;

      while ((row = cursor.getNextRow()) != null) {
        Main main = mapRow(row);
        Date processingDate = main.getProcessingDate();

        if (!processingDate.before(nowAtStartOfDay)) {
          set.add(main);
        }
      }
    } catch (IOException exp) {
      throw new IllegalStateException(
          "There was an issue with retriving a row from table: " + getTableName(), exp
      );
    }

    return set
        .stream()
        .skip(page * pageSize)
        .limit(pageSize)
        .collect(Collectors.toList());
  }

  @Override
  String getTableName() {
    return "CTF_Main";
  }

  @Override
  Main mapRow(Row row) {
    return new Main(row);
  }
}
