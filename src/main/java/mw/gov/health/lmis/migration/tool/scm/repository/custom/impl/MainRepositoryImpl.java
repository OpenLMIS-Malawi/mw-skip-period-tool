package mw.gov.health.lmis.migration.tool.scm.repository.custom.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.repository.custom.MainRepositoryCustom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public class MainRepositoryImpl implements MainRepositoryCustom {

  @Autowired
  @Qualifier("scmEntityManager")
  private EntityManager entityManager;


  @Override
  public List<Main> searchInPeriod(Integer period, Integer page, Integer pageSize) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Main> query = builder.createQuery(Main.class);
    Root<Main> root = query.from(Main.class);

    LocalDateTime current = LocalDate.now().atStartOfDay();
    LocalDateTime previous = current.minusYears(period);

    Path<Facility> facilityField = root.get("id").get("facility");
    Path<LocalDateTime> processingDateField = root.get("id").get("processingDate");

    query.where(
        builder.between(processingDateField, previous, current)
    );

    query.orderBy(
        builder.asc(facilityField),
        builder.asc(processingDateField)
    );

    return entityManager
        .createQuery(query)
        .setFirstResult(page)
        .setMaxResults(pageSize)
        .getResultList();
  }

}
