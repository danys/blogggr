package com.blogggr.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * The primary key class for the friends database table.
 */
@Embeddable
@Getter
@Setter
public class FriendPk implements Serializable {

  //default serial version id, required for serializable classes.
  private static final long serialVersionUID = 1L;

  @Column(name = "user_one_id", insertable = false, updatable = false)
  private Long userOneId;

  @Column(name = "user_two_id", insertable = false, updatable = false)
  private Long userTwoId;

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof FriendPk)) {
      return false;
    }
    FriendPk castOther = (FriendPk) other;
    return
        this.userOneId.equals(castOther.userOneId)
            && this.userTwoId.equals(castOther.userTwoId);
  }

  public int hashCode() {
    final int prime = 31;
    int hash = 17;
    hash = hash * prime + this.userOneId.hashCode();
    hash = hash * prime + this.userTwoId.hashCode();

    return hash;
  }
}