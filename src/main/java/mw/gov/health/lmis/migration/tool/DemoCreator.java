package mw.gov.health.lmis.migration.tool;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import mw.gov.health.lmis.migration.tool.config.ToolProgramMapping;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.BaseEntity;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Facility;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicLevel;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.GeographicZone;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProcessingSchedule;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.RequisitionGroup;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.SupervisoryNode;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityTypeRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisGeographicLevelRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisGeographicZoneRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableDisplayCategoryRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingScheduleRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisRequisitionGroupProgramScheduleRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisRequisitionGroupRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisSupervisoryNodeRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.util.ReferenceDataCreator;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.util.RequsitionUtil;
import mw.gov.health.lmis.migration.tool.scm.domain.CategoryProductJoin;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.domain.Product;
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
  private ReferenceDataCreator referenceDataCreator;

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
  private OlmisProcessingScheduleRepository olmisProcessingScheduleRepository;

  @Autowired
  private OlmisSupervisoryNodeRepository olmisSupervisoryNodeRepository;

  @Autowired
  private OlmisRequisitionGroupRepository olmisRequisitionGroupRepository;

  @Autowired
  private OlmisRequisitionGroupProgramScheduleRepository
      olmisRequisitionGroupProgramScheduleRepository;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Creates demo data.
   */
  public void createDemoData() {
    if (toolProperties.getConfiguration().getInsertDemoData().isReferenceData()) {
      createReferenceDemoData();
    }

    if (toolProperties.getConfiguration().getInsertDemoData().isRequisition()) {
      createRequisitionDemoData();
    }
  }

  private void createReferenceDemoData() {
    FacilityType facilityType = olmisFacilityTypeRepository.save(
        referenceDataCreator.facilityType()
    );

    GeographicLevel zoneLevel = olmisGeographicLevelRepository
        .save(referenceDataCreator.geographicLevel());

    GeographicZone centralEastZone = olmisGeographicZoneRepository
        .save(referenceDataCreator.geographicZone("central east", zoneLevel));
    GeographicZone centralSouthZone = olmisGeographicZoneRepository
        .save(referenceDataCreator.geographicZone("central west", zoneLevel));
    GeographicZone southEastZone = olmisGeographicZoneRepository
        .save(referenceDataCreator.geographicZone("south east", zoneLevel));
    GeographicZone southWestZone = olmisGeographicZoneRepository
        .save(referenceDataCreator.geographicZone("south west", zoneLevel));
    GeographicZone northernZone = olmisGeographicZoneRepository
        .save(referenceDataCreator.geographicZone("northern", zoneLevel));

    List<GeographicZone> zones = Lists.newArrayList(
        centralEastZone, centralSouthZone, southEastZone, southWestZone, northernZone
    );

    Random random = new Random();

    olmisFacilityRepository.save(facilityRepository
        .findAll()
        .stream()
        .map(facility -> {
          GeographicZone zone = zones.get(random.nextInt(zones.size()));
          return referenceDataCreator.facility(
              facility.getName(), facility.getCode(), facilityType, zone
          );
        })
        .collect(Collectors.toList())
    );

    List<Facility> facilities = Lists.newArrayList();
    facilities.add(olmisFacilityRepository.save(referenceDataCreator.facility(
        "Program", "program", facilityType, zones.get(random.nextInt(zones.size()))
    )));
    facilities.add(olmisFacilityRepository.save(referenceDataCreator.facility(
        "CMST - Central", "cmstc", facilityType, centralEastZone)));
    facilities.add(olmisFacilityRepository.save(referenceDataCreator.facility(
        "CMST - South", "cmsts", facilityType, southWestZone)));
    facilities.add(olmisFacilityRepository.save(referenceDataCreator.facility(
        "CMST - North", "cmstn", facilityType, northernZone)));

    Iterable<SupervisoryNode> supervisoryNodes = olmisSupervisoryNodeRepository.save(facilities
        .stream()
        .map(referenceDataCreator::supervisoryNode)
        .collect(Collectors.toList())
    );

    olmisUserRepository.save(
        referenceDataCreator.user("scm", "supply chain", "manager")
    );

    Iterable<RequisitionGroup> requisitionGroups = olmisRequisitionGroupRepository.save(
        StreamSupport
            .stream(supervisoryNodes.spliterator(), false)
            .map(node -> referenceDataCreator.requisitionGroup(
                node, olmisFacilityRepository.findAll()
            ))
            .collect(Collectors.toList())
    );

    olmisProgramRepository.save(Arrays
        .stream(new String[]{"em", "mal", "fp", "hiv", "tb"})
        .map(referenceDataCreator::program)
        .collect(Collectors.toList())
    );

    ProcessingSchedule processingSchedule = olmisProcessingScheduleRepository.save(
        referenceDataCreator.processingSchedule()
    );

    requisitionGroups
        .forEach(group -> olmisRequisitionGroupProgramScheduleRepository.save(
            StreamSupport
                .stream(olmisProgramRepository.findAll().spliterator(), false)
                .map(program ->
                    referenceDataCreator.requisitionGroupProgramSchedule(
                        group, program, processingSchedule
                    )
                )
                .collect(Collectors.toList())
        ));

    List<Main> mains = mainRepository.findAll();

    olmisProcessingPeriodRepository.save(mains
        .stream()
        .map(Main::getProcessingDate)
        .distinct()
        .sorted()
        .map(date -> referenceDataCreator.processingPeriod(date, processingSchedule))
        .collect(Collectors.toList())
    );

    olmisOrderableDisplayCategoryRepository.save(programRepository
        .findAll()
        .stream()
        .map(referenceDataCreator::orderableDisplayCategory)
        .collect(Collectors.toList())
    );

    olmisOrderableRepository.save(productRepository
        .findAll()
        .stream()
        .map(referenceDataCreator::orderable)
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
              .filter(cat -> {
                mw.gov.health.lmis.migration.tool.scm.domain.Program program = programRepository
                    .findByProgramId(category.getProgram());
                return equalsIgnoreCase(cat, program.getName());
              })
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
                Product product = productRepository.findByProductId(category.getProduct());
                Orderable orderable = olmisOrderableRepository.findFirstByProductCode(
                    new Code(product.getProductId().trim())
                );
                mw.gov.health.lmis.migration.tool.scm.domain.Program prog = programRepository
                    .findByProgramId(category.getProgram());
                OrderableDisplayCategory displayCategory = olmisOrderableDisplayCategoryRepository
                    .findByDisplayName(prog.getName().trim());

                ProgramOrderable programOrderable = olmisProgramOrderableRepository
                    .findByProgramAndProductAndCategory(program, orderable, displayCategory);

                if (null == programOrderable) {
                  olmisProgramOrderableRepository.save(
                      referenceDataCreator.programOrderable(
                          program, orderable, displayCategory, category.getOrder()
                      )
                  );
                }
              });
        });

    olmisStockAdjustmentReasonRepository.save(adjustmentTypeRepository
        .findAll()
        .stream()
        .map(type ->
            StreamSupport
                .stream(olmisProgramRepository.findAll().spliterator(), false)
                .map(program -> new Pair<>(type, program))
                .collect(Collectors.toList())
        )
        .flatMap(Collection::stream)
        .map(pair -> referenceDataCreator.stockAdjustmentReason(pair.right, pair.left))
        .collect(Collectors.toList())
    );
  }

  private void createRequisitionDemoData() {
    olmisRequisitionTemplateRepository.save(StreamSupport
        .stream(olmisProgramRepository.findAll().spliterator(), false)
        .map(BaseEntity::getId)
        .map(requsitionUtil::createTemplate)
        .collect(Collectors.toList())
    );
  }

  @AllArgsConstructor
  private static class Pair<L, R> {
    private final L left;
    private final R right;
  }
}
