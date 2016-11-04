package com.blogggr.services;

import com.blogggr.dao.UserDAO;
import com.blogggr.entities.User;
import com.blogggr.requestdata.UserPostData;
import com.blogggr.utilities.Cryptography;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

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

    //For POST request
    public User createUser(UserPostData userData){
        //Check that userData does not contain nulls
        if ((userData.getFirstName()==null) || (userData.getLastName()==null)
                || (userData.getEmail()==null) || (userData.getPassword()==null)) return null;
        User user = new User();
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        //Compute a 12 character salt
        String salt = Cryptography.computeSHA256Hash(String.valueOf(System.currentTimeMillis())) //UTC time
                .substring(0,12); //chars 0 to 11
        user.setSalt(salt);
        user.setPasswordHash(Cryptography.computeSHA256Hash(userData.getPassword()+salt));
        //Compute a 64 character challenge
        String challenge = Cryptography.computeSHA256Hash(String.valueOf(System.currentTimeMillis()/10)); //UTC time
        user.setChallenge(challenge);
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new Timestamp(now.getTime()); //GMT time
        user.setLastChange(currentTimestamp);
        user.setStatus(0);
        userDAO.save(user);
        return user;
    }

    /*public User getUserByEmail(String email){
        return userDAO.getUserByEmail(email);
    }*/
}
