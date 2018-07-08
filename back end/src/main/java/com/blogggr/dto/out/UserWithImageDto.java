package com.blogggr.dto.out;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 09.06.18.
 */
@Getter
@Setter
public class UserWithImageDto extends UserDto{

  private UserImageDto image;
}
