package com.blogggr.strategies.auth;

import com.blogggr.strategies.AuthorizationStrategy;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 07.11.16.
 */
public class NoAuthorization implements AuthorizationStrategy {

  public NoAuthorization() {
    //
  }

  @Override
  public boolean isAuthorized(Map<String, String> header) {
    return true;
  }

  @Override
  public Long getUserId(Map<String, String> header) {
    return null;
  }

  @Override
  public String getError() {
    return "";
  }
}