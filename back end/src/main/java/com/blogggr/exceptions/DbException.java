package com.blogggr.exceptions;

/**
 * Created by Daniel Sunnen on 25.11.16.
 */
public class DbException extends Exception {

  public DbException(String message) {
    super(message);
  }

  public DbException(String message, Throwable e) {
    super(message, e);
  }
}
