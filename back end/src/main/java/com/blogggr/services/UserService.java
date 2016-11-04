package com.blogggr.services;

import com.blogggr.entities.User;
import com.blogggr.requestdata.UserPostData;
import com.blogggr.validator.UserDataValidator;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
public interface UserService {
    User getUserById(long id);

    User createUser(UserPostData userData);

    //User getUserByEmail(String email);
}
