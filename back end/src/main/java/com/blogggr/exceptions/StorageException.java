package com.blogggr.exceptions;

/**
 * Created by Daniel Sunnen on 24.08.17.
 */
public class StorageException extends Exception {

  public StorageException(String message) {
    super(message);
  }

  public StorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
