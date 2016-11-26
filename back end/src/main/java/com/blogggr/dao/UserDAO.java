package com.blogggr.dao;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;

/**
 * Created by Daniel Sunnen on 25.10.16.
 */
public interface UserDAO extends GenericDAO<User>{

    User getUserByEmail(String email) throws DBException, ResourceNotFoundException;
}
