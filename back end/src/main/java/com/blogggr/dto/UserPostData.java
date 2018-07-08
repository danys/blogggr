package com.blogggr.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
@Getter
@Setter
public class UserPostData {

  @NotNull
  @Size(min = 3, max = 100)
  private String firstName;

  @NotNull
  @Size(min = 3, max = 100)
  private String lastName;

  @NotNull
  @Email
  @Size(min = 6, max = 100)
  private String email;

  @NotNull
  @Email
  @Size(min = 6, max = 100)
  private String emailRepeat;

  @NotNull
  @Size(min = 8, max = 100)
  private String password;

  @NotNull
  @Size(min = 8, max = 100)
  private String passwordRepeat;

  @NotNull
  @Enumerated(EnumType.STRING)
  private UserEnums.Sex sex;

  @NotNull
  @Enumerated(EnumType.STRING)
  private UserEnums.Lang lang;
}
