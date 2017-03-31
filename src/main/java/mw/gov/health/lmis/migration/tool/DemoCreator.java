package mw.gov.health.lmis.migration.tool;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.ToolProgramMapping;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.BaseEntity;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicLevel;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicZone;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityTypeRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisGeographicLevelRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisGeographicZoneRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableDisplayCategoryRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.util.ReferenceDataUtil;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.util.RequsitionUtil;
import mw.gov.health.lmis.migration.tool.scm.domain.CategoryProductJoin;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentTypeRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.CategoryProductJoinRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.FacilityRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.MainRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ProductRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ProgramRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class DemoCreator {

  @Autowired
  private ReferenceDataUtil referenceDataUtil;

  @Autowired
  private RequsitionUtil requsitionUtil;

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private MainRepository mainRepository;

  @Autowired
  private AdjustmentTypeRepository adjustmentTypeRepository;

  @Autowired
  private OlmisOrderableRepository olmisOrderableRepository;

  @Autowired
  private OlmisFacilityRepository olmisFacilityRepository;

  @Autowired
  private OlmisProcessingPeriodRepository olmisProcessingPeriodRepository;

  @Autowired
  private OlmisProgramRepository olmisProgramRepository;

  @Autowired
  private OlmisStockAdjustmentReasonRepository olmisStockAdjustmentReasonRepository;

  @Autowired
  private OlmisRequisitionTemplateRepository olmisRequisitionTemplateRepository;

  @Autowired
  private OlmisFacilityTypeRepository olmisFacilityTypeRepository;

  @Autowired
  private OlmisOrderableDisplayCategoryRepository olmisOrderableDisplayCategoryRepository;

  @Autowired
  private OlmisProgramOrderableRepository olmisProgramOrderableRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryProductJoinRepository categoryProductJoinRepository;

  @Autowired
  private OlmisUserRepository olmisUserRepository;

  @Autowired
  private OlmisGeographicLevelRepository olmisGeographicLevelRepository;

  @Autowired
  private OlmisGeographicZoneRepository olmisGeographicZoneRepository;

  @Autowired
  private ToolProperties toolProperties;

  public void createDemoData() {
    olmisUserRepository.save(
        referenceDataUtil.create("supply chain manager", "supply chain", "manager")
    );

    FacilityType facilityType = olmisFacilityTypeRepository.save(referenceDataUtil.create());

    GeographicLevel zoneLevel = olmisGeographicLevelRepository
        .save(referenceDataUtil.create("zone", 2));

    GeographicZone centralEastZone = olmisGeographicZoneRepository
        .save(referenceDataUtil.create("central east", zoneLevel));
    GeographicZone centralSouthZone = olmisGeographicZoneRepository
        .save(referenceDataUtil.create("central west", zoneLevel));
    GeographicZone southEastZone = olmisGeographicZoneRepository
        .save(referenceDataUtil.create("south east", zoneLevel));
    GeographicZone southWestZone = olmisGeographicZoneRepository
        .save(referenceDataUtil.create("south west", zoneLevel));
    GeographicZone northernZone = olmisGeographicZoneRepository
        .save(referenceDataUtil.create("northern", zoneLevel));

    List<GeographicZone> zones = Lists.newArrayList(
        centralEastZone, centralSouthZone, southEastZone, southWestZone, northernZone
    );

    Random random = new Random();

    olmisFacilityRepository.save(StreamSupport
        .stream(facilityRepository.findAll().spliterator(), false)
        .map(facility -> {
          GeographicZone zone = zones.get(random.nextInt(zones.size()));
          return referenceDataUtil.create(
              facility.getName(), facility.getCode(), facilityType, zone
          );
        })
        .collect(Collectors.toList())
    );

    olmisFacilityRepository.save(referenceDataUtil.create(
        "Program", "program", facilityType, zones.get(random.nextInt(zones.size()))
    ));
    olmisFacilityRepository.save(referenceDataUtil.create(
        "CMST - Central", "cmstc", facilityType, centralEastZone));
    olmisFacilityRepository.save(referenceDataUtil.create(
        "CMST - South", "cmsts", facilityType, southWestZone));
    olmisFacilityRepository.save(referenceDataUtil.create(
        "CMST - North", "cmstn", facilityType, northernZone));

    Iterable<Main> mains = mainRepository.findAll();

    olmisProcessingPeriodRepository.save(StreamSupport
        .stream(mains.spliterator(), false)
        .map(Main::getId)
        .map(Main.ComplexId::getProcessingDate)
        .distinct()
        .sorted()
        .map(referenceDataUtil::create)
        .collect(Collectors.toList())
    );

    olmisOrderableDisplayCategoryRepository.save(StreamSupport
        .stream(programRepository.findAll().spliterator(), false)
        .map(referenceDataUtil::create)
        .collect(Collectors.toList())
    );

    olmisOrderableRepository.save(StreamSupport
        .stream(productRepository.findAll().spliterator(), false)
        .map(referenceDataUtil::create)
        .collect(Collectors.toList())
    );

    olmisProgramRepository.save(Arrays
        .stream(new String[]{"em", "mal", "fp", "hiv", "tb"})
        .map(referenceDataUtil::create)
        .collect(Collectors.toList())
    );

    Multimap<String, CategoryProductJoin> groups = HashMultimap.create();
    for (CategoryProductJoin category : categoryProductJoinRepository.findAll()) {
      toolProperties
          .getMapping()
          .getPrograms()
          .stream()
          .filter(cp -> null != cp
              .getCategories()
              .stream()
              .filter(cat -> equalsIgnoreCase(cat, category.getProgram().getName()))
              .findFirst()
              .orElse(null)
          )
          .map(ToolProgramMapping::getCode)
          .forEach(code -> groups.put(code, category));
    }

    groups
        .asMap()
        .forEach((code, categories) -> {
          Program program =
              olmisProgramRepository.findByCode(new Code(code));

          categories
              .forEach(category -> {
                Orderable orderable = olmisOrderableRepository.findFirstByName(
                    category.getProduct().getName()
                );

                OrderableDisplayCategory displayCategory = olmisOrderableDisplayCategoryRepository
                    .findByDisplayName(category.getProgram().getName());

                ProgramOrderable programOrderable = olmisProgramOrderableRepository
                    .findByProgramAndProductAndCategory(program, orderable, displayCategory);

                if (null == programOrderable) {
                  olmisProgramOrderableRepository.save(
                      referenceDataUtil.create(
                          program, orderable, displayCategory, category.getOrder(), 5
                      )
                  );
                }
              });
        });

    olmisStockAdjustmentReasonRepository.save(StreamSupport
        .stream(adjustmentTypeRepository.findAll().spliterator(), false)
        .map(type ->
            StreamSupport
                .stream(olmisProgramRepository.findAll().spliterator(), false)
                .map(program -> new Pair<>(type, program))
                .collect(Collectors.toList())
        )
        .flatMap(Collection::stream)
        .map(pair -> referenceDataUtil.create(pair.getRight(), pair.getLeft()))
        .collect(Collectors.toList())
    );

    olmisRequisitionTemplateRepository.save(StreamSupport
        .stream(olmisProgramRepository.findAll().spliterator(), false)
        .map(BaseEntity::getId)
        .map(requsitionUtil::createTemplate)
        .collect(Collectors.toList())
    );
  }

}
