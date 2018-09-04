package com.blogggr.dto.out;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnlyPostDto {

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

  private Long postId;
  private String shortTitle;
  private String textBody;
  private String timestamp;
  private String title;

  public void setTimestamp(Timestamp ts) {
    if (ts == null) {
      this.timestamp = "";
    } else {
      this.timestamp = dateFormat.format(ts);
    }
  }
}
