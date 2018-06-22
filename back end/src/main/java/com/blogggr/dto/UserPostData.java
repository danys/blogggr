package com.blogggr.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
@Getter
@Setter
public class UserPostData {

  @NotNull
  private String firstName;

  @NotNull
  private String lastName;

  @NotNull
  @Email
  private String email;

  @NotNull
  @Email
  private String emailRepeat;

  @NotNull
  private String password;

  @NotNull
  private String passwordRepeat;

  @NotNull
  private String sex;

  @NotNull
  private String lang;
}
