package com.blogggr.dao;

import com.blogggr.entities.User;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
public interface UserDAO extends GenericDAO<User>{

    User getUserByEmail(String email);
}
