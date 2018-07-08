package com.blogggr.dto.out;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 09.06.18.
 */
@Getter
@Setter
public class PostImageDto {

  private Long postImageId;
  private String name;
  private Integer width;
  private Integer height;
}
