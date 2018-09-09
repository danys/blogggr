package com.blogggr.dto.out;

import com.blogggr.entities.User;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 10.06.18.
 */
public class FriendDto {

  @Getter
  private Long userId1;

  @Getter
  private Long userId2;

  @Getter
  @Setter
  private Integer status;

  @Getter
  private Long lastActionUserId;

  public void setUserId1(User user){
    this.userId1 = user.getUserId();
  }

  public void setUserId2(User user){
    this.userId2 = user.getUserId();
  }

  public void setLastActionUserId(User user){
    this.lastActionUserId = user.getUserId();
  }
}
