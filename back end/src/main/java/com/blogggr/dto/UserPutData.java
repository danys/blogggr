package com.blogggr.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 06.07.17.
 */
@Getter
@Setter
public class UserPutData {

  @Size(min = 3, max = 100)
  private String firstName;

  @Size(min = 3, max = 100)
  private String lastName;

  @Email
  @Size(min = 6, max = 100)
  private String email;

  @Size(min = 8, max = 100)
  private String oldPassword;

  @Size(min = 8, max = 100)
  private String password;

  @Size(min = 8, max = 100)
  private String passwordRepeat;

  @Enumerated(EnumType.STRING)
  private UserEnums.Lang lang;
}
