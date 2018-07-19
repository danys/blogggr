package com.blogggr.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the friends database table.
 */
@Entity
@Table(name = "friends", schema = "blogggr")
@Getter
@Setter
public class Friend implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private FriendPk id = new FriendPk();

  /**
   * Status meaning: 0 = pending 1 = accepted 2 = declined 3 = blocked.
   */
  private Integer status;

  @ManyToOne
  @JoinColumn(name = "user_one_id")
  @MapsId("userOneId")
  private User user1;

  @ManyToOne
  @JoinColumn(name = "user_two_id")
  @MapsId("userTwoId")
  private User user2;

  @ManyToOne
  @JoinColumn(name = "last_action_user_id")
  private User lastActionUserId;

  @Column(name = "last_action_timestamp")
  private Timestamp lastActionTimestamp;

  @Version
  private Long version;
}