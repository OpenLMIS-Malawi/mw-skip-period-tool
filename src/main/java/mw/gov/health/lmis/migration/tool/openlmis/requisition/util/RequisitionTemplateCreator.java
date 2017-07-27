package mw.gov.health.lmis.migration.tool.openlmis.requisition.util;

import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.RequisitionTemplateColumnConfiguration;
import mw.gov.health.lmis.migration.tool.config.RequisitionTemplateConfiguration;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Code;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.domain.Program;
import mw.gov.health.lmis.migration.tool.openlmis.referencedata.repository.ProgramRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.AvailableRequisitionColumn;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplate;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.domain.RequisitionTemplateColumn;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.AvailableRequisitionColumnRepository;
import mw.gov.health.lmis.migration.tool.openlmis.requisition.repository.RequisitionTemplateRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RequisitionTemplateCreator {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequisitionTemplateCreator.class);
  private static final String ALL_PROGRAMS = "__ALL_PROGRAMS__";

  @Autowired
  private AvailableRequisitionColumnRepository availableRequisitionColumnRepository;

  @Autowired
  private RequisitionTemplateRepository requisitionTemplateRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ToolProperties toolProperties;

  /**
   * Creates new templates for all available programs.
   */
  public void createTemplates() {
    List<RequisitionTemplateConfiguration> templates = toolProperties
        .getParameters()
        .getRequisitionTemplates();

    for (RequisitionTemplateConfiguration config : templates) {
      List<Program> programs = getPrograms(config);

      for (Program program : programs) {
        requisitionTemplateRepository.save(createTemplate(config, program.getId()));
      }
    }
  }

  private List<Program> getPrograms(RequisitionTemplateConfiguration config) {
    List<Program> programs;

    if (ALL_PROGRAMS.equalsIgnoreCase(config.getProgram())) {
      programs = programRepository.findAll();
    } else {
      Code code = new Code(config.getProgram());
      Program program = programRepository.findByCode(code);

      if (null == program) {
        LOGGER.error("Can't find program by code: {}", code);
        programs = Lists.newArrayList();
      } else {
        programs = Lists.newArrayList(program);
      }
    }

    return programs;
  }

  private RequisitionTemplate createTemplate(RequisitionTemplateConfiguration config,
                                             UUID programId) {
    LOGGER.info("Create requisition template for program: {}", programId);

    ZoneId zoneId = toolProperties.getParameters().getTimeZone().toZoneId();
    ZonedDateTime createdDate = toolProperties.getParameters().getStartDate().atStartOfDay(zoneId);

    Map<String, RequisitionTemplateColumn> columnMap = config
        .getColumns()
        .entrySet()
        .stream()
        .map(entry -> new ImmutablePair<>(entry.getKey(), createColumn(entry.getValue())))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    RequisitionTemplate template = new RequisitionTemplate();
    template.setProgramId(programId);
    template.setNumberOfPeriodsToAverage(config.getNumberOfPeriodsToAverage());
    template.setColumnsMap(columnMap);
    template.setCreatedDate(createdDate);
    template.setModifiedDate(createdDate);

    return template;
  }

  private RequisitionTemplateColumn createColumn(RequisitionTemplateColumnConfiguration config) {
    AvailableRequisitionColumn columnDefinition = availableRequisitionColumnRepository
        .findOne(UUID.fromString(config.getDefinition()));

    RequisitionTemplateColumn requisitionTemplateColumn = new RequisitionTemplateColumn();
    requisitionTemplateColumn.setName(config.getName());
    requisitionTemplateColumn.setLabel(config.getLabel());
    requisitionTemplateColumn.setIndicator(columnDefinition.getIndicator());
    requisitionTemplateColumn.setDisplayOrder(config.getDisplayOrder());
    requisitionTemplateColumn.setIsDisplayed(config.getDisplayed());
    requisitionTemplateColumn.setSource(config.getSource());
    requisitionTemplateColumn.setDefinition(columnDefinition.getDefinition());
    requisitionTemplateColumn.setColumnDefinition(columnDefinition);

    if (!isEmpty(columnDefinition.getOptions())) {
      requisitionTemplateColumn.setOption(columnDefinition.getOptions().iterator().next());
    }

    return requisitionTemplateColumn;
  }

}
