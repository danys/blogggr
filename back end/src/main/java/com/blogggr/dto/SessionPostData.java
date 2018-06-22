package com.blogggr.dto;

import javax.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@Getter
@Setter
public class SessionPostData {

  @Email
  private String email;
  private String password;
  private Boolean rememberMe;
}
