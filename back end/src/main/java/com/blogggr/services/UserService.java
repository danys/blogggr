package com.blogggr.services;

import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.SessionExpiredException;
import com.blogggr.requestdata.UserPostData;

import java.util.List;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
public interface UserService {

    User getUserById(long id);

    User createUser(UserPostData userData);

    User getUserByEmail(String email) throws ResourceNotFoundException, DBException;

    User getUserBySessionHash(String sessionHash) throws ResourceNotFoundException, DBException, SessionExpiredException;

    void updateUser(long userResourceID, long userID, UserPostData userData) throws ResourceNotFoundException, DBException, NotAuthorizedException;

    List<User> getUsers(String searchString);
}
