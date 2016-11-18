package com.blogggr.services;

import com.blogggr.entities.Session;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import com.blogggr.requestdata.SessionPostData;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public interface SessionService {

    Session createSession(SessionPostData sessionData) throws ResourceNotFoundException, WrongPasswordException;

    void deleteSession(long sessionId, long userID) throws ResourceNotFoundException, NotAuthorizedException;
}
