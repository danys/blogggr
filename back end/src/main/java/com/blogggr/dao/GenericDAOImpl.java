package com.blogggr.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GenericDAOImpl<T extends Serializable> implements GenericDao<T> {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

  public T update(T entity) {
    return entityManager.merge(entity);
  }

  public void delete(T entity) {
    entityManager.remove(entity);
  }

  public void deleteById(Long entityId) {
    T entity = findById(entityId);
    delete(entity);
  }
}
