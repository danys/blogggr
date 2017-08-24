package com.blogggr.exceptions;

/**
 * Created by Daniel Sunnen on 25.11.16.
 */
public class DBException extends Exception {

  public DBException(String message) {
    super(message);
  }

  public DBException(String message, Throwable e) {
    super(message, e);
  }
}
