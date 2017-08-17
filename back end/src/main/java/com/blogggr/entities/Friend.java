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


/**
 * The persistent class for the friends database table.
 */
@Entity
@Table(name = "friends", schema = "blogggr")
public class Friend implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private FriendPk id;

  /**
   * Status meaning: 0 = pending 1 = accepted 2 = declined 3 = blocked.
   */
  private Integer status;

  //bi-directional many-to-one association to User
  @ManyToOne
  @JoinColumn(name = "user_one_id")
  @MapsId("userOneID")
  private User user1;

  //bi-directional many-to-one association to User
  @ManyToOne
  @JoinColumn(name = "user_two_id")
  @MapsId("userTwoID")
  private User user2;

  //bi-directional many-to-one association to User
  @ManyToOne
  @JoinColumn(name = "last_action_user_id")
  private User lastActionUserId;

  @Column(name = "last_action_timestamp")
  private Timestamp lastActionTimestamp;

  @Version
  private Long version;

  public Friend() {
  }

  public FriendPk getId() {
    return this.id;
  }

  public void setId(FriendPk id) {
    this.id = id;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public User getUser1() {
    return this.user1;
  }

  public void setUser1(User user1) {
    this.user1 = user1;
  }

  public User getUser2() {
    return this.user2;
  }

  public void setUser2(User user2) {
    this.user2 = user2;
  }

  public User getLastActionUserId() {
    return lastActionUserId;
  }

  public void setLastActionUserId(User lastActionUserId) {
    this.lastActionUserId = lastActionUserId;
  }

  public Timestamp getLastActionTimestamp() {
    return lastActionTimestamp;
  }

  public void setLastActionTimestamp(Timestamp lastActionTimestamp) {
    this.lastActionTimestamp = lastActionTimestamp;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

}