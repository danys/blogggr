package com.blogggr.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
public abstract class GenericDaoImpl<T extends Serializable> implements GenericDao<T> {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @PersistenceContext
  protected EntityManager entityManager;

  protected Class<T> clazz;

  public GenericDaoImpl(Class<T> clazz) {
    this.clazz = clazz;
  }

  public T findById(Long id) {
    logger.debug("GenericDaoImpl - findById: {}, clazz: {}", id, clazz.getName());
    return entityManager.find(clazz, id);
  }

  public List<T> findAll() {
    logger.debug("GenericDaoImpl - findAll, clazz: {}", clazz.getName());
    return entityManager.createQuery("from " + clazz.getName()).getResultList();
  }

  public void save(T entity) {
    logger.debug("GenericDaoImpl - save, clazz: {}", clazz.getName());
    entityManager.persist(entity);
  }

  public T update(T entity) {
    logger.debug("GenericDaoImpl - update, clazz: {}", clazz.getName());
    return entityManager.merge(entity);
  }

  public void delete(T entity) {
    logger.debug("GenericDaoImpl - delete, clazz: {}", clazz.getName());
    entityManager.remove(entity);
  }

  public void deleteById(Long entityId) {
    logger.debug("GenericDaoImpl - deleteById, clazz: {}", clazz.getName());
    T entity = findById(entityId);
    delete(entity);
  }
}
