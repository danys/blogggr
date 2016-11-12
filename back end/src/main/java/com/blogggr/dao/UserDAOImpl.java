package com.blogggr.dao;

import com.blogggr.entities.User;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
@Repository
public class UserDAOImpl extends GenericDAOImpl<User> implements UserDAO{

    public UserDAOImpl(){
        super(User.class);
    }

    public User getUserByEmail(String email){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get("email"),email));
        return entityManager.createQuery(query).getSingleResult();
    }
}
