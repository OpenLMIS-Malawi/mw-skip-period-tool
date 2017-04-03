package mw.gov.health.lmis.migration.tool.scm.service;

import com.google.common.collect.Multimap;

import mw.gov.health.lmis.migration.tool.scm.domain.Item;

import java.util.Collection;
import java.util.Date;

public interface ItemService {

  Multimap<String, Item> groupByCategory(Date processingDate, String facility);

  Double getMonthsOfStock(Item item);

  String getNotes(Collection<Item> item);

}
