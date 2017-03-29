package mw.gov.health.lmis.migration.tool;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import mw.gov.health.lmis.migration.tool.openlmis.BaseEntity;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Orderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityTypeApprovedProductRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityTypeRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramOrderableRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.custom.OlmisOrderableDisplayCategoryRepository;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.util.ReferenceDataUtil;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.util.RequsitionUtil;
import mw.gov.health.lmis.migration.tool.scm.domain.Main;
import mw.gov.health.lmis.migration.tool.scm.repository.AdjustmentTypeRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.CategoryProductJoinRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.FacilityRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.MainRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ProductRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ProgramRepository;
import mw.gov.health.lmis.migration.tool.scm.util.Grouping;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootApplication
public class AppConfiguration {

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
  private OlmisFacilityTypeApprovedProductRepository olmisFacilityTypeApprovedProductRepository;

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

  /**
   * Here the application starts with spring context.
   */
  @Bean
  public CommandLineRunner commandLineRunner(JobLauncher jobLauncher, Job mainTransformJob) {
    return args -> {
      // create demo data (it will be removed in future)
      createDemoData();

      // run the transform job
      jobLauncher.run(mainTransformJob, new JobParameters());
    };
  }

  private void createDemoData() {
    olmisUserRepository.save(
        referenceDataUtil.create("supply chain manager", "supply chain", "manager")
    );

    FacilityType facilityType = olmisFacilityTypeRepository.save(referenceDataUtil.create());

    olmisFacilityRepository.save(StreamSupport
        .stream(facilityRepository.findAll().spliterator(), false)
        .map(facility -> referenceDataUtil.create(
            facility.getName(), facility.getCode(), facilityType
        ))
        .collect(Collectors.toList())
    );

    Iterable<Main> mains = mainRepository.findAll();

    olmisProcessingPeriodRepository.save(StreamSupport
        .stream(mains.spliterator(), false)
        .map(Main::getId)
        .map(Main.ComplexId::getProcessingDate)
        .distinct()
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

    Grouping
        .groupByCategoryName(
            categoryProductJoinRepository.findAll(), cat -> cat.getProgram().getName()
        )
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
