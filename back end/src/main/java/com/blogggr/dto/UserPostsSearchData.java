package com.blogggr.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 09.06.18.
 */
@Getter
@Setter
public class UserPostsSearchData extends PrevNextData<Long>{

  private Long userId; //the logged in user
  private Long posterUserId;
}
