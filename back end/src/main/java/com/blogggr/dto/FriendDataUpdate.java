package com.blogggr.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 23.06.18.
 */
@Getter
@Setter
public class FriendDataUpdate extends FriendDataBase{

  @NotNull
  @Min(1)
  @Max(3)
  private Integer action;
}
