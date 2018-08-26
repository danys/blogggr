package com.blogggr.dto.out;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 09.06.18.
 */
@Getter
@Setter
public class PostDto {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

  private Long postId;
  private String shortTitle;
  private String textBody;
  private String timestamp;
  private String title;
  private List<CommentDto> comments;
  private List<PostImageDto> postImages;
  private UserDto user;

  public void setTimestamp(Timestamp ts) {
    this.timestamp = dateFormat.format(ts);
  }
}
