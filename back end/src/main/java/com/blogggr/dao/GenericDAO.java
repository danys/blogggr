package com.blogggr.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
public interface GenericDAO<T extends Serializable> {

    public T findById(Long id);

    public List<T> findAll();

    public void save(T entity);

    public void update(T entity);

    public void delete(T entity);

    public void deleteById(Long entityId);
}
