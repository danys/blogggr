package com.blogggr.services;

import com.blogggr.config.AppConfig;
import com.blogggr.dao.SessionDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Session;
import com.blogggr.entities.User;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import com.blogggr.requestdata.SessionPostData;
import com.blogggr.utilities.Cryptography;
import com.blogggr.utilities.TimeUtilities;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SessionServiceImpl implements SessionService{

    private SessionDAO sessionDAO;
    private UserDAO userDAO;

    public SessionServiceImpl(SessionDAO sessionDAO, UserDAO userDAO){
        this.sessionDAO = sessionDAO;
        this.userDAO = userDAO;
    }

    @Override
    public Session createSession(SessionPostData sessionData) throws ResourceNotFoundException, WrongPasswordException{
        Session session = new Session();
        User user = userDAO.getUserByEmail(sessionData.getEmail());
        if (user==null) throw new ResourceNotFoundException("User not found!");
        //Check that the supplied password is correct
        String storedPasswordHash = user.getPasswordHash();
        String storedSalt = user.getSalt();
        String submitPasswordHash = Cryptography.computeSHA256Hash(sessionData.getPassword()+storedSalt);
        if (submitPasswordHash.compareTo(storedPasswordHash)!=0) throw new WrongPasswordException("Supplied password is wrong!");
        Timestamp ts = TimeUtilities.getCurrentTimestamp();
        Long millis = ts.getTime();
        Timestamp validTill = new Timestamp(millis+ AppConfig.sessionValidityMillis);
        session.setUser(user);
        session.setValidtill(validTill);
        session.setValid(true);
        String hash = Cryptography.computeSHA256Hash(String.valueOf(user.getUserID())+String.valueOf(validTill));
        session.setSessionhash(hash);
        sessionDAO.save(session);
        return session;
    }

    @Override
    public void deleteSession(long sessionId){
        sessionDAO.deleteById(sessionId);
    }
}
