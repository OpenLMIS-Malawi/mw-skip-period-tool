package org.openlmis.migration.tool.openlmis;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.springframework.data.repository.CrudRepository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.TooManyMethods")
public abstract class InMemoryRepository<T extends BaseEntity> implements CrudRepository<T, UUID> {
  protected Map<UUID, T> database = Maps.newConcurrentMap();

  @Override
  public <S extends T> S save(S entity) {
    entity.setId(UUID.randomUUID());

    database.put(entity.getId(), entity);

    return entity;
  }

  @Override
  public <S extends T> Iterable<S> save(Iterable<S> entities) {
    entities.forEach(this::save);

    return entities;
  }

  @Override
  public T findOne(UUID id) {
    return database.get(id);
  }

  @Override
  public boolean exists(UUID id) {
    return database.containsKey(id);
  }

  @Override
  public Iterable<T> findAll() {
    return database.values();
  }

  @Override
  public Iterable<T> findAll(Iterable<UUID> ids) {
    return database
        .entrySet()
        .stream()
        .filter(entry -> Iterables.contains(ids, entry.getKey()))
        .map(Map.Entry::getValue)
        .collect(Collectors.toList());
  }

  @Override
  public long count() {
    return database.size();
  }

  @Override
  public void delete(UUID id) {
    database.remove(id);
  }

  @Override
  public void delete(T entity) {
    database.remove(entity.getId());
  }

  @Override
  public void delete(Iterable<? extends T> entities) {
    entities.forEach(this::delete);
  }

  @Override
  public void deleteAll() {
    database.clear();
  }

}
