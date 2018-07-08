package com.blogggr.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.security.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 09.06.18.
 */
@Getter
@Setter
public class PostDto {

  private Long postId;
  private String shortTitle;
  private String textBody;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private Timestamp timestamp;
  private String title;
  private List<CommentDto> comments;
  private List<PostImageDto> postImages;
  private UserDto user;
}
