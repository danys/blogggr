package com.blogggr.strategies;

import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import java.io.UnsupportedEncodingException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 23.08.17.
 */
public abstract class ServiceInvocation implements ServiceInvocationStrategy{

  @Override
  public Object invokeFileService(MultipartFile file, Long userID)
      throws DBException, ResourceNotFoundException, WrongPasswordException, NotAuthorizedException, UnsupportedEncodingException{
    return null;
  }
}
