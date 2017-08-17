package com.blogggr.entities;

import com.blogggr.json.JsonTransformer;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the comments database table.
 */
@Entity
@Table(name = "comments", schema = "blogggr")
public class Comment implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "comment_id")
  @SequenceGenerator(name = "seq", sequenceName = "blogggr.comment_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
  private Long commentId;

  private String text;

  private Timestamp timestamp;

  //bi-directional many-to-one association to Post
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;

  //bi-directional many-to-one association to User
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Version
  private Long version;

  public Comment() {
  }

  public Long getCommentId() {
    return this.commentId;
  }

  public void setCommentId(Long commentId) {
    this.commentId = commentId;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTimestamp() {
    return JsonTransformer.timestampToString(timestamp);
  }

  @JsonIgnore
  public Timestamp getRealTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public Post getPost() {
    return this.post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}