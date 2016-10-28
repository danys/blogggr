package com.blogggr.services;

import com.blogggr.entities.User;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
public interface UserService {
    User getUserById(long id);

    User getUserByEmail(String email);
}
