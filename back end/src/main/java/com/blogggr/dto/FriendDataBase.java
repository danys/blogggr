package com.blogggr.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 23.06.18.
 */
@Getter
@Setter
public class FriendDataBase {

  @NotNull
  private Long userId1;

  @NotNull
  private Long userId2;
}
