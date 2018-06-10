package com.blogggr.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 06.07.17.
 */
@Getter
@Setter
public class UserPutData {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String oldPassword;
  private String password;
  private String passwordRepeat;
}
