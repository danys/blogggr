package com.blogggr.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
public interface GenericDAO<T extends Serializable> {

    T findById(Long id);

    List<T> findAll();

    void save(T entity);

    void update(T entity);

    void delete(T entity);

    void deleteById(Long entityId);
}
