package com.blogggr.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the users database table.
 */
@Entity
@Table(name = "users", schema = "blogggr")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "user_id")
  @SequenceGenerator(name = "seq", sequenceName = "blogggr.user_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
  private Long userId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  private String email;

  @Column(name = "password_hash", columnDefinition = "bpchar(64)")
  private String passwordHash;

  @Column(columnDefinition = "bpchar(12)")
  private String salt;

  @Column(columnDefinition = "bpchar(64)")
  private String challenge;

  private Integer status;

  private Integer sex; //0=male, 1=female

  private String lang;

  @Column(name = "last_change")
  private Timestamp lastChange;

  //bi-directional many-to-one association to Comment
  @JsonIgnore
  @OneToMany(mappedBy = "user")
  private List<Comment> comments;

  //bi-directional many-to-one association to Friend
  @JsonIgnore
  @OneToMany(mappedBy = "user1")
  private List<Friend> friends1;

  //bi-directional many-to-one association to Friend
  @JsonIgnore
  @OneToMany(mappedBy = "user2")
  private List<Friend> friends2;

  //bi-directional many-to-one association to Friend
  @JsonIgnore
  @OneToMany(mappedBy = "lastActionUserId")
  private List<Friend> friends3;

  //bi-directional many-to-one association to Post
  @JsonIgnore
  @OneToMany(mappedBy = "user")
  private List<Post> posts;

  @Version
  private Long version;

  public User() {
  }

  public Long getUserId() {
    return this.userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getChallenge() {
    return this.challenge;
  }

  public void setChallenge(String challenge) {
    this.challenge = challenge;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstname) {
    this.firstName = firstname;
  }

  public Timestamp getLastChange() {
    return this.lastChange;
  }

  public void setLastChange(Timestamp lastchange) {
    this.lastChange = lastchange;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastname) {
    this.lastName = lastname;
  }

  public String getPasswordHash() {
    return this.passwordHash;
  }

  public void setPasswordHash(String passwordhash) {
    this.passwordHash = passwordhash;
  }

  public String getSalt() {
    return this.salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getSex() {
    return sex;
  }

  public void setSex(Integer sex) {
    this.sex = sex;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public List<Comment> getComments() {
    return this.comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public Comment addComment(Comment comment) {
    getComments().add(comment);
    comment.setUser(this);

    return comment;
  }

  public Comment removeComment(Comment comment) {
    getComments().remove(comment);
    comment.setUser(null);

    return comment;
  }

  public List<Friend> getFriends1() {
    return this.friends1;
  }

  public void setFriends1(List<Friend> friends1) {
    this.friends1 = friends1;
  }

  public Friend addFriends1(Friend friends1) {
    getFriends1().add(friends1);
    friends1.setUser1(this);

    return friends1;
  }

  public Friend removeFriends1(Friend friends1) {
    getFriends1().remove(friends1);
    friends1.setUser1(null);

    return friends1;
  }

  public List<Friend> getFriends2() {
    return this.friends2;
  }

  public void setFriends2(List<Friend> friends2) {
    this.friends2 = friends2;
  }

  public Friend addFriends2(Friend friends2) {
    getFriends2().add(friends2);
    friends2.setUser2(this);

    return friends2;
  }

  public Friend removeFriends2(Friend friends2) {
    getFriends2().remove(friends2);
    friends2.setUser2(null);

    return friends2;
  }

  public List<Friend> getFriends3() {
    return this.friends3;
  }

  public void setFriends3(List<Friend> friends3) {
    this.friends3 = friends3;
  }

  public Friend addFriends3(Friend friends3) {
    getFriends3().add(friends3);
    friends3.setLastActionUserId(this);
    return friends3;
  }

  public Friend removeFriends3(Friend friends3) {
    getFriends3().remove(friends3);
    friends3.setLastActionUserId(null);

    return friends3;
  }

  public List<Post> getPosts() {
    return this.posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public Post addPost(Post post) {
    getPosts().add(post);
    post.setUser(this);

    return post;
  }

  public Post removePost(Post post) {
    getPosts().remove(post);
    post.setUser(null);

    return post;
  }

}