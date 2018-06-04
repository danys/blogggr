package com.blogggr.requestdata;

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
  private String email;

  @NotNull
  private String password;

  @NotNull
  private String passwordRepeat;

  @NotNull
  private String sex;

  @NotNull
  private String lang;
}
