package com.blogggr.dto.out;

import java.security.Timestamp;
import java.util.List;

/**
 * Created by Daniel Sunnen on 09.06.18.
 */
public class PostDto {

  private Long postId;
  private String shortTitle;
  private String textBody;
  private Timestamp timestamp;
  private String title;
  private List<CommentDto> comments;
  private List<PostImageDto> postImages;
  private UserDto user;
}
