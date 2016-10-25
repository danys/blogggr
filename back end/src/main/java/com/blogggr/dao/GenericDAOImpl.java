package com.blogggr.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
public abstract class GenericDAOImpl<T extends Serializable> implements GenericDAO<T>{

    @PersistenceContext
    EntityManager entityManager;

    private Class< T > clazz;

    public GenericDAOImpl(Class<T> clazz){
        this.clazz = clazz;
    }

    public T findById(Long id){
        return entityManager.find(clazz, id);
    }

    public List< T > findAll(){
        return entityManager.createQuery( "from " + clazz.getName()).getResultList();
    }

    public void save(T entity){
        entityManager.persist(entity);
    }

    public void update(T entity){
        entityManager.merge(entity);
    }

    public void delete(T entity){
        entityManager.remove(entity);
    }

    public void deleteById(Long entityId){
        T entity = findById(entityId);
        delete( entity );
    }
}
