package com.blogggr.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
public class UserDAO {

    @PersistenceContext
    EntityManager entityManager;

    User findById(){

    }
}
