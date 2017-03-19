package com.blogggr.services;

import com.blogggr.config.AppConfig;
import com.blogggr.dao.SessionDAO;
import com.blogggr.dao.UserDAO;
import com.blogggr.entities.Session;
import com.blogggr.entities.User;
import com.blogggr.exceptions.*;
import com.blogggr.requestdata.SessionPostData;
import com.blogggr.requestdata.SessionPutData;
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
    public Session createSession(SessionPostData sessionData) throws ResourceNotFoundException, DBException, WrongPasswordException{
        Session session = new Session();
        User user = userDAO.getUserByEmail(sessionData.getEmail());
        //Check that the supplied password is correct
        String storedPasswordHash = user.getPasswordHash();
        String storedSalt = user.getSalt();
        String submitPasswordHash = Cryptography.computeSHA256Hash(sessionData.getPassword()+storedSalt);
        if (submitPasswordHash.compareTo(storedPasswordHash)!=0) throw new WrongPasswordException("Supplied password is wrong!");
        Timestamp ts = TimeUtilities.getCurrentTimestamp();
        Long millis = ts.getTime();
        long millisOffset = (!sessionData.getRememberMe())?AppConfig.sessionValidityMillis:AppConfig.sessionValidityMillis*10;
        Timestamp validTill = new Timestamp(millis+millisOffset);
        session.setUser(user);
        session.setValidtill(validTill);
        session.setValid(true);
        String hash = Cryptography.computeSHA256Hash(String.valueOf(user.getUserID())+String.valueOf(validTill));
        session.setSessionhash(hash);
        sessionDAO.save(session);
        return session;
    }

    //Delete a session by its primary key
    @Override
    public void deleteSession(long sessionId, long userID) throws ResourceNotFoundException, NotAuthorizedException{
        Session session = sessionDAO.findById(sessionId);
        if (session==null) throw new ResourceNotFoundException("Session not found!");
        //Check if the session user is assigned to this session
        if (session.getUser().getUserID()!=userID) throw new NotAuthorizedException("Invalid session id!");
        sessionDAO.deleteById(sessionId);
    }

    //Update session by primary key
    @Override
    public void updateSession(long sessionId, long userID, SessionPutData sessionData) throws ResourceNotFoundException, NotAuthorizedException, SessionExpiredException{
        Session session = sessionDAO.findById(sessionId);
        if (session==null) throw new ResourceNotFoundException("Session not found!");
        //Check if the session user is assigned to this session
        if (session.getUser().getUserID()!=userID) throw new NotAuthorizedException("Invalid session id!");
        //Check that the session as not yet expired
        Timestamp ts = TimeUtilities.getCurrentTimestamp();
        if (session.getValidtill().compareTo(ts)<0){
            throw new SessionExpiredException();
        }
        session.setValidtill(new Timestamp(sessionData.getValidTill()));
        sessionDAO.update(session);
    }
}
