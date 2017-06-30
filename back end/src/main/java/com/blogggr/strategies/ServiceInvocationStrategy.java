package com.blogggr.strategies;

import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public interface ServiceInvocationStrategy {

    Object invokeService(Map<String,String> input, String body, Long userID) throws DBException, ResourceNotFoundException, WrongPasswordException, NotAuthorizedException, UnsupportedEncodingException;
}
