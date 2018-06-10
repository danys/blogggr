package com.blogggr.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
@Getter
@Setter
public class CommentData {

  @NotNull
  private Long postId;

  @NotNull
  @Size(min = 4)
  private String text;
}
