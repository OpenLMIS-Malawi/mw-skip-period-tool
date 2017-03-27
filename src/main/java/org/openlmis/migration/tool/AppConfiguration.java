package org.openlmis.migration.tool;

import com.beust.jcommander.internal.Lists;

import org.openlmis.migration.tool.openlmis.BaseEntity;
import org.openlmis.migration.tool.openlmis.referencedata.domain.FacilityType;
import org.openlmis.migration.tool.openlmis.referencedata.domain.FacilityTypeApprovedProduct;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.openlmis.migration.tool.openlmis.referencedata.domain.OrderableDisplayCategory;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProgramOrderable;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityTypeApprovedProductRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityTypeRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProgramOrderableRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.custom.OlmisOrderableDisplayCategoryRepository;
import org.openlmis.migration.tool.openlmis.referencedata.util.ReferenceDataUtil;
import org.openlmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import org.openlmis.migration.tool.openlmis.requisition.util.RequsitionUtil;
import org.openlmis.migration.tool.scm.domain.Main;
import org.openlmis.migration.tool.scm.repository.AdjustmentTypeRepository;
import org.openlmis.migration.tool.scm.repository.FacilityRepository;
import org.openlmis.migration.tool.scm.repository.ItemRepository;
import org.openlmis.migration.tool.scm.repository.MainRepository;
import org.openlmis.migration.tool.scm.repository.ProductRepository;
import org.openlmis.migration.tool.scm.repository.ProgramRepository;
import org.openlmis.migration.tool.scm.util.ItemUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
  private ItemRepository itemRepository;

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
    FacilityType facilityType = olmisFacilityTypeRepository.save(referenceDataUtil.create());

    StreamSupport
        .stream(facilityRepository.findAll().spliterator(), false)
        .map(facility -> referenceDataUtil.create(
            facility.getName(), facility.getCode(), facilityType
        ))
        .forEach(olmisFacilityRepository::save);

    StreamSupport
        .stream(mainRepository.findAll().spliterator(), false)
        .map(Main::getId)
        .map(Main.ComplexId::getProcessingDate)
        .distinct()
        .map(referenceDataUtil::create)
        .forEach(olmisProcessingPeriodRepository::save);

    StreamSupport
        .stream(programRepository.findAll().spliterator(), false)
        .map(referenceDataUtil::create)
        .forEach(olmisOrderableDisplayCategoryRepository::save);

    StreamSupport
        .stream(productRepository.findAll().spliterator(), false)
        .map(referenceDataUtil::create)
        .forEach(olmisOrderableRepository::save);

    StreamSupport
        .stream(mainRepository.findAll().spliterator(), false)
        .map(main -> itemRepository.findByProcessingDateAndFacility(
            main.getId().getProcessingDate(), main.getId().getFacility()
        ))
        .map(ItemUtil::groupByProgram)
        .map(programs -> {
          List<FacilityTypeApprovedProduct> approvedProducts = Lists.newArrayList();

          programs
              .asMap()
              .forEach((code, items) -> {
                Program program = olmisProgramRepository.save(referenceDataUtil.create(code));

                items
                    .forEach(item -> {
                      Orderable orderable = olmisOrderableRepository.findFirstByName(
                          item.getProduct().getName()
                      );

                      OrderableDisplayCategory category = olmisOrderableDisplayCategoryRepository
                          .findByDisplayName(item.getCategoryProduct().getProgram().getName());

                      ProgramOrderable programOrderable = olmisProgramOrderableRepository.save(
                          referenceDataUtil.create(
                              program, orderable, category, item.getCategoryProduct().getOrder(), 5
                          )
                      );

                      approvedProducts.add(
                          referenceDataUtil.create(facilityType, programOrderable)
                      );
                    });
              });

          return approvedProducts;
        })
        .forEach(olmisFacilityTypeApprovedProductRepository::save);

    StreamSupport
        .stream(adjustmentTypeRepository.findAll().spliterator(), false)
        .map(type ->
            StreamSupport
                .stream(olmisProgramRepository.findAll().spliterator(), false)
                .map(program -> new Pair<>(type, program))
                .collect(Collectors.toList())
        )
        .flatMap(Collection::stream)
        .map(pair -> referenceDataUtil.create(pair.getRight(), pair.getLeft()))
        .forEach(olmisStockAdjustmentReasonRepository::save);

    StreamSupport
        .stream(olmisProgramRepository.findAll().spliterator(), false)
        .map(BaseEntity::getId)
        .map(requsitionUtil::createTemplate)
        .forEach(olmisRequisitionTemplateRepository::save);
  }

  @AllArgsConstructor
  @Getter
  private static class Pair<L, R> {
    private final L left;
    private final R right;
  }

}
