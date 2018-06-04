package com.blogggr.strategies;

import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 23.08.17.
 */
public abstract class FileServiceInvocation implements ServiceInvocationStrategy{

  @Override
  public final Object invokeService(Map<String, String> input, String body, Long userID)
      throws DbException, ResourceNotFoundException, WrongPasswordException, NotAuthorizedException, UnsupportedEncodingException{
    return null;
  }
}
