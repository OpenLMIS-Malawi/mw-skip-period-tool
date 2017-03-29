package mw.gov.health.lmis.migration.tool.scm.repository;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ReadOnlyRepositoryTest {

  private ReadOnlyRepository<Object, Long> repository = new ReadOnlyRepository<Object, Long>() {

    @Override
    public Iterable<Object> findAll(Sort sort) {
      return null;
    }

    @Override
    public Page<Object> findAll(Pageable pageable) {
      return null;
    }

    @Override
    public Object findOne(Long aLong) {
      return null;
    }

    @Override
    public boolean exists(Long aLong) {
      return false;
    }

    @Override
    public Iterable<Object> findAll() {
      return null;
    }

    @Override
    public Iterable<Object> findAll(Iterable<Long> longs) {
      return null;
    }

    @Override
    public long count() {
      return 0;
    }

  };

  private Object entity = new Object();
  private Object entity2 = new Object();
  private Long id = 10L;

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotAllowToSaveInstance() throws Exception {
    repository.save(entity);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotAllowToSaveCollectionOfInstances() throws Exception {
    repository.save(Lists.newArrayList(entity, entity2));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotAllowToDeleteInstanceById() throws Exception {
    repository.delete(10L);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotAllowToDeleteInstanceByEntity() throws Exception {
    repository.delete(entity);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotAllowToDeleteCollectionOfInstances() throws Exception {
    repository.delete(Lists.newArrayList(entity, entity2));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotAllowToDeleteAll() throws Exception {
    repository.deleteAll();
  }
}
