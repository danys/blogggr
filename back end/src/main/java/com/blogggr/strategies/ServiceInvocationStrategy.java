package com.blogggr.strategies;

import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.StorageException;
import com.blogggr.exceptions.WrongPasswordException;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public interface ServiceInvocationStrategy {

  Object invokeService(Map<String, String> input, String body, Long userID)
      throws DbException, ResourceNotFoundException, WrongPasswordException, NotAuthorizedException, UnsupportedEncodingException;

  Object invokeFileService(MultipartFile file, Long userID)
      throws DbException, ResourceNotFoundException, WrongPasswordException, NotAuthorizedException, UnsupportedEncodingException, StorageException;
}
