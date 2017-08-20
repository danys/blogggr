package com.blogggr.services;

import com.blogggr.exceptions.*;
import com.blogggr.requestdata.SessionPostData;

import java.io.UnsupportedEncodingException;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public interface SessionService {

  SessionServiceImpl.SessionDetails createSession(SessionPostData sessionData)
      throws ResourceNotFoundException, DBException, WrongPasswordException, UnsupportedEncodingException; //Resource not found if user cannot be found
}
