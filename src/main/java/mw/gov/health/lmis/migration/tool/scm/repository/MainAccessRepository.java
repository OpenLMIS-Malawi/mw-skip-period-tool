package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.config.ToolParameters;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Predicate;

@Repository
public class MainAccessRepository extends BaseAccessRepository<Main> {
  private final SearchInPeriodPredicate searchInPeriodPredicate = new SearchInPeriodPredicate();

  private Instant startDate;
  private Instant endDate;

  /**
   * Creates new instance with passing tool properties.
   */
  @Autowired
  public MainAccessRepository(ToolProperties toolProperties) {
    ToolParameters parameters = toolProperties.getParameters();
    ZoneId zoneId = parameters.getTimeZone().toZoneId();

    startDate = parameters.getStartDate().atStartOfDay(zoneId).toInstant();
    endDate = parameters.getEndDate().atTime(23,59,59).atZone(zoneId).toInstant();
  }

  /**
   * Find mains that have processing date in the given period.
   */
  public List<Main> searchInPeriod() {
    return findAll(searchInPeriodPredicate);
  }

  @Override
  String getTableName() {
    return properties.getTableNames().getMain();
  }

  @Override
  Main mapRow(Row row) {
    return new Main(row);
  }

  private final class SearchInPeriodPredicate implements Predicate<Main> {

    @Override
    public boolean test(Main main) {
      Instant processingDate = main.getProcessingDate().toInstant();
      return !processingDate.isBefore(startDate) && !processingDate.isAfter(endDate);
    }

  }
}
