package org.openlmis.migration.tool.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

@NoRepositoryBean
public interface ReadOnlyRepository<T, I extends Serializable>
    extends PagingAndSortingRepository<T, I> {

  @Override
  default <S extends T> S save(S entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  default <S extends T> Iterable<S> save(Iterable<S> entities) {
    throw new UnsupportedOperationException();
  }

  @Override
  default void delete(I id) {
    throw new UnsupportedOperationException();
  }

  @Override
  default void delete(T entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  default void delete(Iterable<? extends T> entities) {
    throw new UnsupportedOperationException();
  }

  @Override
  default void deleteAll() {
    throw new UnsupportedOperationException();
  }

}
