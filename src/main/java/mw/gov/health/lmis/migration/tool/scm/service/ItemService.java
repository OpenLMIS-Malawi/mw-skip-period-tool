package mw.gov.health.lmis.migration.tool.scm.service;

import mw.gov.health.lmis.migration.tool.scm.domain.Comment;
import mw.gov.health.lmis.migration.tool.scm.domain.Item;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ItemService {

  List<Item> search(Date processingDate, String facility);

  Map<String, Collection<Item>> groupByCategory(List<Item> items);

  String getNotes(String note, List<Comment> comments);

}
