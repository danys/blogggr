package com.blogggr.services;

import com.blogggr.dao.UserDAO;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.models.PrevNextListPage;
import com.blogggr.models.RandomAccessListPage;
import com.blogggr.requestdata.UserPostData;
import com.blogggr.requestdata.UserPutData;
import com.blogggr.requestdata.UserSearchData;
import com.blogggr.utilities.Cryptography;
import com.blogggr.utilities.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by Daniel Sunnen on 28.10.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService{

    private UserDAO userDAO;
    private final Log logger = LogFactory.getLog(this.getClass());

    public UserServiceImpl(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    @Override
    public User getUserById(long id){
        return userDAO.findById(id);
    }

    @Override
    public User getUserByEmail(String email) throws ResourceNotFoundException, DBException{
        return userDAO.getUserByEmail(email);
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
        user.setSex(Integer.parseInt(userData.getSex()));
        user.setLang(userData.getLang());
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
    public void updateUser(long userResourceID, long userID, UserPutData userData) throws ResourceNotFoundException, DBException, NotAuthorizedException{
        User user = userDAO.findById(userResourceID);
        if (user == null) throw new ResourceNotFoundException("User not found!");
        //A user can only change his own data
        if (user.getUserId() != userID) throw new NotAuthorizedException("Not authorized to change this user!");
        //If an old password has been provided check it!
        if (userData.getOldPassword()!=null){
            String oldHash = Cryptography.computeSHA256Hash(userData.getOldPassword() + user.getSalt());
            if (oldHash.compareTo(user.getPasswordHash())!=0) throw new NotAuthorizedException("Old password is wrong!");
        }
        if (userData.getPassword() != null){
            if (userData.getOldPassword()==null) throw new NotAuthorizedException("Old password must be provided!");
            user.setPasswordHash(Cryptography.computeSHA256Hash(userData.getPassword() + user.getSalt()));
        }
        if (userData.getEmail() != null) user.setEmail(userData.getEmail());
        if (userData.getLastName() != null) user.setLastName(userData.getLastName());
        if (userData.getFirstName() != null) user.setFirstName(userData.getFirstName());
        user.setLastChange(TimeUtilities.getCurrentTimestamp());
        userDAO.save(user);
    }

    @Override
    public RandomAccessListPage<User> getUsers(String searchString, Integer limit, Integer pageNumber) throws DBException{
        RandomAccessListPage<User> usersPage = userDAO.getUsers(searchString,limit,pageNumber);
        return usersPage;
    }

    @Override
    public PrevNextListPage<User> getUsersBySearchTerms(UserSearchData searchData) throws DBException{
        return userDAO.getUsersBySearchTerms(searchData);
    }
}
