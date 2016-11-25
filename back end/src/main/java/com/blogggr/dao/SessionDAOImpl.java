package com.blogggr.dao;

import com.blogggr.entities.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@Repository
public class SessionDAOImpl extends GenericDAOImpl<Session> implements SessionDAO{

    public SessionDAOImpl(){
        super(Session.class);
    }

    public Session getSessionBySessionHash(String sessionHash) throws RuntimeException{
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Session> query = cb.createQuery(Session.class);
        Root<Session> root = query.from(Session.class);
        query.where(cb.equal(root.get("sessionHash"),sessionHash));
        return entityManager.createQuery(query).getSingleResult();
    }
}
