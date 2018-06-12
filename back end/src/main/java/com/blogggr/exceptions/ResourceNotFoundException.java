package com.blogggr.exceptions;

/**
 * Created by Daniel Sunnen on 12.11.16.
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
