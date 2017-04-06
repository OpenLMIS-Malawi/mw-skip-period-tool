package mw.gov.health.lmis.migration.tool.scm.service;

import com.google.common.collect.Multimap;

import mw.gov.health.lmis.migration.tool.scm.domain.Item;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ItemService {

  Multimap<String, Item> groupByCategory(List<Item> items);

  List<Item> search(Date processingDate, String facility);

  String getNotes(Collection<Item> item);

}
