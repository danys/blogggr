package com.blogggr.dto.out;

import java.sql.Timestamp;

/**
 * Created by Daniel Sunnen on 09.06.18.
 */
public class CommentDto {

  private Long commentId;
  private String text;
  private Timestamp timestamp;
  private UserDto user;
}
