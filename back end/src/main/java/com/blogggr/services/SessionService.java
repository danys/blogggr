package com.blogggr.services;

import com.blogggr.entities.Session;
import com.blogggr.exceptions.*;
import com.blogggr.requestdata.SessionPostData;
import com.blogggr.requestdata.SessionPutData;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public interface SessionService {

    Session createSession(SessionPostData sessionData) throws ResourceNotFoundException, DBException, WrongPasswordException; //Resource not found if user cannot be found

    void deleteSession(long sessionId, long userID) throws ResourceNotFoundException, NotAuthorizedException;

    void updateSession(long sessionId, long userID, SessionPutData sessionData) throws ResourceNotFoundException, NotAuthorizedException, SessionExpiredException;
}
