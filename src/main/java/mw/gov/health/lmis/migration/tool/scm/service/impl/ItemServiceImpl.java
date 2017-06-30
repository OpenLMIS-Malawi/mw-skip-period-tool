package mw.gov.health.lmis.migration.tool.scm.service.impl;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.migration.tool.config.ToolProgramMapping;
import mw.gov.health.lmis.migration.tool.config.ToolProperties;
import mw.gov.health.lmis.migration.tool.scm.domain.CategoryProductJoin;
import mw.gov.health.lmis.migration.tool.scm.domain.Comment;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;
import mw.gov.health.lmis.migration.tool.scm.domain.Program;
import mw.gov.health.lmis.migration.tool.scm.repository.CategoryProductJoinAccessRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ItemAccessRepository;
import mw.gov.health.lmis.migration.tool.scm.repository.ProgramAccessRepository;
import mw.gov.health.lmis.migration.tool.scm.service.ItemService;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

  @Autowired
  private CategoryProductJoinAccessRepository categoryProductJoinRepository;

  @Autowired
  private ProgramAccessRepository programRepository;

  @Autowired
  private ItemAccessRepository itemRepository;

  @Autowired
  private ToolProperties toolProperties;

  @Override
  public List<Item> search(Date processingDate, String facility) {
    return itemRepository.search(processingDate, facility);
  }

  @Override
  public Map<String, Collection<Item>> groupByCategory(List<Item> items) {
    Multimap<String, Item> groups = HashMultimap.create();
    for (int i = 0, size = items.size(); i < size; i++) {
      Item item = items.get(i);

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

    return groups.asMap();
  }

  @Override
  public String getNotes(String note, List<Comment> comments) {
    List<String> notes = Lists.newArrayList();
    notes.add(note);
    notes.addAll(comments.stream().map(Comment::getComment).collect(Collectors.toList()));
    notes.removeIf(StringUtils::isBlank);

    return notes.isEmpty() ? null : notes.stream().collect(Collectors.joining("; "));
  }

  private String getCategoryName(Item item) {
    CategoryProductJoin join = categoryProductJoinRepository.findById(item.getCategoryProduct());

    if (null == join) {
      Map<Integer, Integer> mapping = toolProperties.getMapping().getCategoryProductJoins();
      Integer id = null == mapping ? null : mapping.get(item.getCategoryProduct());

      if (null == id) {
        LOGGER.error(
            "Can't find Category Product Join by default value: {}",
            item.getCategoryProduct()
        );
        return null;
      }

      LOGGER.warn(
          "Can't find Category Product Join by default value: {}. Use mapping value: {}",
          item.getCategoryProduct(), id
      );

      join = categoryProductJoinRepository.findById(id);

      if (null == join) {
        LOGGER.error("Can't find Category Product Join for by mapping value: {}", id);
        return null;
      }
    }

    Program program = programRepository.findByProgramId(join.getProgram());

    return program.getName();
  }

}
