package com.blogggr.dto.out;

import com.blogggr.entities.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 10.06.18.
 */
public class FriendDto {

  @Getter
  private Long userId1;

  @Setter
  private Long userId2;

  @Getter
  @Setter
  private Integer status;

  @Getter
  @Setter
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private Timestamp lastActionTimestamp;

  public void setUserId1(User user){
    this.userId1 = user.getUserId();
  }

  public void setUserId2(User user){
    this.userId2 = user.getUserId();
  }
}