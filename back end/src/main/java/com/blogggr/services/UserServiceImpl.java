package com.blogggr.services;

import com.blogggr.dao.GenericDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService{

    private UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public User getUserById(long id){
        return userDAO.findById(id);
    }

    public User getUserByEmail(String email){
        return userDAO.getUserByEmail(email);
    }

    public User storeUser(User user){
        userDAO.save(user);
        return user;
    }
}
