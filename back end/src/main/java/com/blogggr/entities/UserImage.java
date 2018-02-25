package com.blogggr.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
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
 * Created by Daniel Sunnen on 19.08.17.
 */
@Entity
@Table(name = "user_images", schema = "blogggr")
public class UserImage implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "user_image_id")
  @SequenceGenerator(name = "seqUserImage", sequenceName = "blogggr.user_image_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUserImage")
  private Long userImageId;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "name")
  private String name;

  @Column(name = "is_current")
  private Boolean isCurrent;

  @Column(name = "width")
  private Integer width;

  @Column(name = "height")
  private Integer height;

  @Version
  private Long version;

  //Getters and setters

  public Long getUserImageId() {
    return userImageId;
  }

  public void setUserImageId(Long userImageId) {
    this.userImageId = userImageId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getCurrent() {
    return isCurrent;
  }

  public void setCurrent(Boolean current) {
    isCurrent = current;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }
}
