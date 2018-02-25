package com.blogggr.entities;

import com.blogggr.json.JsonTransformer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the posts database table.
 */
@Entity
@Table(name = "posts", schema = "blogggr")
public class Post implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "post_id")
  @SequenceGenerator(name = "seqPost", sequenceName = "blogggr.post_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqPost")
  private Long postId;

  @Column(name = "short_title")
  private String shortTitle;

  @Column(name = "text_body")
  private String textBody;

  private Timestamp timestamp;

  private String title;

  @Column(name = "is_global")
  private Boolean isGlobal;

  //bi-directional many-to-one association to Comment
  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
  private List<Comment> comments;

  //bi-directional many-to-one association to Post
  @OneToMany(mappedBy = "post")
  private List<PostImage> postImages;

  //bi-directional many-to-one association to User
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Version
  private Long version;

  public Post() {
  }

  //Getters and setters

  public String getTimestamp() {
    return JsonTransformer.timestampToString(timestamp);
  }

  public Long getPostId() {
    return this.postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public String getShortTitle() {
    return this.shortTitle;
  }

  public void setShortTitle(String shorttitle) {
    this.shortTitle = shorttitle;
  }

  public String getTextBody() {
    return this.textBody;
  }

  public void setTextBody(String textbody) {
    this.textBody = textbody;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Boolean getGlobal() {
    return isGlobal;
  }

  public void setGlobal(Boolean global) {
    isGlobal = global;
  }

  public List<Comment> getComments() {
    return this.comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public Comment addComment(Comment comment) {
    getComments().add(comment);
    comment.setPost(this);

    return comment;
  }

  public Comment removeComment(Comment comment) {
    getComments().remove(comment);
    comment.setPost(null);

    return comment;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<PostImage> getPostImages() {
    return postImages;
  }

  public void setPostImages(List<PostImage> postImages) {
    this.postImages = postImages;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}