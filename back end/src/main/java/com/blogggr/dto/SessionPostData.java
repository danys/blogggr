package com.blogggr.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@Getter
@Setter
public class SessionPostData {

  private String email;
  private String password;
  private Boolean rememberMe;

  public SessionPostData() {
    //
  }
}
