package org.openlmis.migration.tool;

import org.openlmis.migration.tool.openlmis.BaseEntity;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Facility;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Orderable;
import org.openlmis.migration.tool.openlmis.referencedata.domain.ProcessingPeriod;
import org.openlmis.migration.tool.openlmis.referencedata.domain.Program;
import org.openlmis.migration.tool.openlmis.referencedata.domain.StockAdjustmentReason;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisFacilityRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisOrderableRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProcessingPeriodRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisProgramRepository;
import org.openlmis.migration.tool.openlmis.referencedata.repository.OlmisStockAdjustmentReasonRepository;
import org.openlmis.migration.tool.openlmis.referencedata.util.ReferenceDataUtil;
import org.openlmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import org.openlmis.migration.tool.openlmis.requisition.repository.OlmisRequisitionTemplateRepository;
import org.openlmis.migration.tool.openlmis.requisition.util.RequsitionUtil;
import org.openlmis.migration.tool.scm.domain.AdjustmentType;
import org.openlmis.migration.tool.scm.domain.Main;
import org.openlmis.migration.tool.scm.repository.AdjustmentTypeRepository;
import org.openlmis.migration.tool.scm.repository.FacilityRepository;
import org.openlmis.migration.tool.scm.repository.ItemRepository;
import org.openlmis.migration.tool.scm.repository.MainRepository;
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
  private JobLauncher jobLauncher;

  @Autowired
  private Job mainTransformJob;

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

  /**
   * Here the application starts with spring context.
   */
  @Bean
  public CommandLineRunner commandLineRunner() {
    return args -> {
      // create demo data (it will be removed in future)
      createDemoData();

      // run the transform job
      jobLauncher.run(mainTransformJob, new JobParameters());
    };
  }

  private void createDemoData() {
    List<Orderable> orderables = StreamSupport
        .stream(itemRepository.findAll().spliterator(), false)
        .map(referenceDataUtil::create)
        .collect(Collectors.toList());

    olmisOrderableRepository.save(orderables);

    List<Facility> facilities = StreamSupport
        .stream(facilityRepository.findAll().spliterator(), false)
        .map(facility -> referenceDataUtil.create(facility.getName(), facility.getCode()))
        .collect(Collectors.toList());

    olmisFacilityRepository.save(facilities);

    List<ProcessingPeriod> periods = StreamSupport
        .stream(mainRepository.findAll().spliterator(), false)
        .map(Main::getId)
        .map(Main.ComplexId::getProcessingDate)
        .map(referenceDataUtil::create)
        .collect(Collectors.toList());

    olmisProcessingPeriodRepository.save(periods);

    List<Program> programs = StreamSupport
        .stream(mainRepository.findAll().spliterator(), false)
        .map(referenceDataUtil::create)
        .collect(Collectors.toList());

    olmisProgramRepository.save(programs);

    List<StockAdjustmentReason> reasons = StreamSupport
        .stream(adjustmentTypeRepository.findAll().spliterator(), false)
        .map(type ->
            programs
                .stream()
                .map(program -> new Pair(type, program))
                .collect(Collectors.toList())
        )
        .flatMap(Collection::stream)
        .map(pair -> referenceDataUtil.create(pair.getProgram(), pair.getType()))
        .collect(Collectors.toList());

    olmisStockAdjustmentReasonRepository.save(reasons);

    List<RequisitionTemplate> templates = programs
        .stream()
        .map(BaseEntity::getId)
        .map(requsitionUtil::createTemplate)
        .collect(Collectors.toList());

    olmisRequisitionTemplateRepository.save(templates);
  }

  @AllArgsConstructor
  @Getter
  private static class Pair {
    private final AdjustmentType type;
    private final Program program;
  }

}
