package com.blogggr.dto.out;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 07.06.18.
 */
@Getter
@Setter
public class UserDto {

  private Long userId;
  private String email;
  private String firstName;
  private String lastName;
}
