package com.blogggr.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GenericDAOImpl<T extends Serializable> implements GenericDAO<T> {

  private final Log logger = LogFactory.getLog(this.getClass());

  @PersistenceContext
  protected EntityManager entityManager;

  protected Class<T> clazz;

  public GenericDAOImpl(Class<T> clazz) {
    this.clazz = clazz;
  }

  public T findById(Long id) {
    return entityManager.find(clazz, id);
  }

  public List<T> findAll() {
    return entityManager.createQuery("from " + clazz.getName()).getResultList();
  }

  public void save(T entity) {
    entityManager.persist(entity);
  }

  public void update(T entity) {
    entityManager.merge(entity);
  }

  public void delete(T entity) {
    entityManager.remove(entity);
  }

  public void deleteById(Long entityId) {
    T entity = findById(entityId);
    delete(entity);
  }
}
