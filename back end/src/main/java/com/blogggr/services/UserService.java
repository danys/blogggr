package com.blogggr.services;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.SessionExpiredException;
import com.blogggr.requestdata.UserPostData;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
public interface UserService {

    User getUserById(long id);

    User createUser(UserPostData userData);

    User getUserByEmail(String email);

    User getUserBySessionHash(String sessionHash) throws DBException, SessionExpiredException;
}
