package com.blogggr.services;

import com.blogggr.dao.GenericDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.User;
import com.blogggr.validator.UserDataValidator;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
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

    public User storeUser(UserDataValidator userData){
        if (!userData.validate()) return null;
        User user = new User();
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        user.setChallenge("blaChallenge");
        user.setPasswordHash("blaHash");
        user.setSalt("ghejrltoxmya");
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new Timestamp(now.getTime());
        user.setLastChange(currentTimestamp);
        user.setStatus(0);
        userDAO.save(user);
        return user;
    }
}
