package com.blogggr.dto.out;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostWithCommentImageDto {

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

  private Long postId;
  private String shortTitle;
  private String textBody;
  private String timestamp;
  private String title;
  private List<CommentWithImageDto> comments;
  private List<PostImageDto> postImages;
  private UserWithImageDto user;

  public void setTimestamp(Timestamp ts) {
    if (ts == null) {
      this.timestamp = "";
    } else {
      this.timestamp = dateFormat.format(ts);
    }
  }
}