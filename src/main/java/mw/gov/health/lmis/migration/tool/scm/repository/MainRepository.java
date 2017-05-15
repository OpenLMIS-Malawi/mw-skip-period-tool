package mw.gov.health.lmis.migration.tool.scm.repository;

import com.healthmarketscience.jackcess.Row;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;

import java.util.Date;
import java.util.List;

@Repository
public class MainRepository extends BaseRepository<Main> {
  private Date startDate;
  private Date endDate;

  /**
   * Creates new instance with passing tool properties.
   */
  @Autowired
  public MainRepository(ToolProperties toolProperties) {
    startDate = toolProperties.getParameters().getStartDate();
    endDate = toolProperties.getParameters().getEndDate();
  }

  /**
   * Find mains that have processing date in the given period.
   */
  public List<Main> searchInPeriod() {
    return search(main -> !main.getProcessingDate().before(startDate)
        && !main.getProcessingDate().after(endDate));
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
