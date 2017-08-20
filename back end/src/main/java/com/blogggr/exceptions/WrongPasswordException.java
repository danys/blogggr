package com.blogggr.exceptions;

/**
 * Created by Daniel Sunnen on 14.11.16.
 */
public class WrongPasswordException extends Exception {

  public WrongPasswordException(String message) {
    super(message);
  }
}
