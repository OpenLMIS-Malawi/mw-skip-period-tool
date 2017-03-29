package org.openlmis.migration.tool;

import com.beust.jcommander.internal.Lists;

import org.openlmis.migration.tool.openlmis.BaseEntity;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Code;
import org.openlmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import org.openlmis.migration.tool.openlmis.referencedata.domain.FacilityTypeApprovedProduct;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.openlmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityTypeApprovedProductRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityTypeRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProgramOrderableRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisUserRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.custom.OlmisOrderableDisplayCategoryRepository;
import org.openlmis.migration.tool.openlmis.referencedata.util.ReferenceDataUtil;
import org.openlmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import org.openlmis.migration.tool.openlmis.requisition.util.RequsitionUtil;
import org.openlmis.migration.tool.scm.domain.Main;
import org.openlmis.migration.tool.scm.repository.AdjustmentTypeRepository;
import org.openlmis.migration.tool.scm.repository.CategoryProductJoinRepository;
import org.openlmis.migration.tool.scm.repository.FacilityRepository;
import org.openlmis.migration.tool.scm.repository.MainRepository;
import org.openlmis.migration.tool.scm.repository.ProductRepository;
import org.openlmis.migration.tool.scm.repository.ProgramRepository;
import org.openlmis.migration.tool.scm.util.Grouping;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
        referenceDataUtil.create("supply_chain_manager", "supply chain", "manager")
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

    List<FacilityTypeApprovedProduct> approvedProducts = Lists.newArrayList();
    Grouping
        .groupByCategoryName(
            categoryProductJoinRepository.findAll(), cat -> cat.getProgram().getName()
        )
        .asMap()
        .forEach((code, categories) -> {
          org.openlmis.migration.tool.openlmis.referencedata.domain.Program program =
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
                  programOrderable = olmisProgramOrderableRepository.save(
                      referenceDataUtil.create(
                          program, orderable, displayCategory, category.getOrder(), 5
                      )
                  );
                }

                FacilityTypeApprovedProduct approvedProduct =
                    olmisFacilityTypeApprovedProductRepository
                        .findByFacilityTypeAndProgramOrderable(facilityType, programOrderable);

                if (null == approvedProduct) {
                  approvedProducts.add(
                      referenceDataUtil.create(facilityType, programOrderable)
                  );
                }
              });
        });

    olmisFacilityTypeApprovedProductRepository.save(approvedProducts);

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

  @AllArgsConstructor
  @Getter
  private static class Pair<L, R> {
    private final L left;
    private final R right;
  }

}
