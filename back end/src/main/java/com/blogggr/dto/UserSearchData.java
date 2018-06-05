package com.blogggr.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 01.08.17.
 */
@Getter
@Setter
public class UserSearchData extends PrevNextData<Long> {

  private String firstName;
  private String lastName;
  private String email;
}
