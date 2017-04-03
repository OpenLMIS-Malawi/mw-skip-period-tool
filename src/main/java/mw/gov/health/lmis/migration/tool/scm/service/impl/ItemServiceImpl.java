package mw.gov.health.lmis.migration.tool.scm.service.impl;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.migration.tool.config.ToolProgramMapping;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.CategoryProductJoin;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Program;
import mw.gov.health.lmis.migration.tool.scm.repository.CategoryProductJoinRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.CommentRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ItemRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ProgramRepository;
import mw.gov.health.lmis.migration.tool.scm.service.ItemService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

  @Autowired
  private CategoryProductJoinRepository categoryProductJoinRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public Multimap<String, Item> groupByCategory(Date processingDate, String facility) {
    List<Item> items = itemRepository.search(processingDate, facility);

    Multimap<String, Item> groups = HashMultimap.create();
    for (Item item : items) {
      toolProperties
          .getMapping()
          .getPrograms()
          .stream()
          .filter(cp -> null != cp
              .getCategories()
              .stream()
              .filter(cat -> equalsIgnoreCase(cat, getCategoryName(item)))
              .findFirst()
              .orElse(null)
          )
          .map(ToolProgramMapping::getCode)
          .forEach(code -> groups.put(code, item));
    }

    return groups;
  }

  @Override
  public Double getMonthsOfStock(Item item) {
    if (0 == item.getClosingBalance() || 0 == item.getAdjustedDispensedQuantity()) {
      return BigDecimal.ZERO.doubleValue();
    }

    return BigDecimal.valueOf(item.getClosingBalance())
        .divide(
            BigDecimal.valueOf(item.getAdjustedDispensedQuantity()),
            1,
            BigDecimal.ROUND_HALF_UP
        )
        .doubleValue();
  }

  @Override
  public String getNotes(Collection<Item> items) {
    List<String> notes = Lists.newArrayList();

    for (Item item : items) {
      notes.add(item.getNote());

      commentRepository.search(item.getId())
          .forEach(comment -> notes.add(
              comment.getType() + ": " + comment.getComment()
          ));
    }

    notes.removeIf(StringUtils::isBlank);

    return notes.isEmpty() ? null : notes.stream().collect(Collectors.joining("; "));
  }

  private String getCategoryName(Item item) {
    CategoryProductJoin join = categoryProductJoinRepository.findById(item.getCategoryProduct());
    Program program = programRepository.findByProgramId(join.getProgram());

    return program.getName();
  }

}
