package com.blogggr.services;

import com.blogggr.dao.SessionDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Session;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.SessionExpiredException;
import com.blogggr.requestdata.UserPostData;
import com.blogggr.utilities.Cryptography;
import com.blogggr.utilities.TimeUtilities;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService{

    private UserDAO userDAO;
    private SessionDAO sessionDAO;

    public UserServiceImpl(UserDAO userDAO, SessionDAO sessionDAO){
        this.userDAO = userDAO;
        this.sessionDAO = sessionDAO;
    }

    @Override
    public User getUserById(long id){
        return userDAO.findById(id);
    }

    @Override
    public User getUserByEmail(String email) throws ResourceNotFoundException, DBException{
        return userDAO.getUserByEmail(email);
    }

    @Override
    public User getUserBySessionHash(String sessionHash) throws ResourceNotFoundException, DBException, SessionExpiredException{
        Session session = sessionDAO.getSessionBySessionHash(sessionHash);
        //Check if user session is expired
        Timestamp ts = TimeUtilities.getCurrentTimestamp();
        if (session.getValidtill().compareTo(ts)<0){
            throw new SessionExpiredException();
        }
        return session.getUser();
    }

    //For POST request
    @Override
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
        Timestamp currentTimestamp = TimeUtilities.getCurrentTimestamp();
        user.setLastChange(currentTimestamp);
        user.setStatus(0);
        userDAO.save(user);
        return user;
    }

    @Override
    public void updateUser(long userResourceID, long userID, UserPostData userData) throws ResourceNotFoundException, DBException, NotAuthorizedException{
        User user = userDAO.findById(userResourceID);
        if (user == null) throw new ResourceNotFoundException("User not found!");
        //A user can only change his own data
        if (user.getUserID() != userID) throw new NotAuthorizedException("Not authorized to change this user!");
        if (userData.getPassword() != null)
                user.setPasswordHash(Cryptography.computeSHA256Hash(userData.getPassword() + user.getSalt()));
        if (userData.getEmail() != null) user.setEmail(userData.getEmail());
        if (userData.getLastName() != null) user.setLastName(userData.getLastName());
        if (userData.getFirstName() != null) user.setFirstName(userData.getFirstName());
        user.setLastChange(TimeUtilities.getCurrentTimestamp());
        userDAO.save(user);
    }

    @Override
    public List<User> getUsers(String searchString){
        return null;
    }
}
