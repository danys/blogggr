package com.blogggr.entities;

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
import javax.persistence.Transient;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the users database table.
 */
@Entity
@Table(name = "users", schema = "blogggr")
@Getter
@Setter
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final String WOMAN_NAME = "woman.png";
  private static final String MAN_NAME = "man.png";
  private static final int IMG_WIDTH = 128;
  private static final int IMG_HEIGHT = 128;

  @Id
  @Column(name = "user_id")
  @SequenceGenerator(name = "seqUser", sequenceName = "blogggr.user_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUser")
  private Long userId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  private String email;

  @Column(name = "password_hash", columnDefinition = "bpchar(64)")
  private String passwordHash;

  @Column(columnDefinition = "bpchar(64)")
  private String challenge;

  private Integer status;

  private Integer sex; //0=male, 1=female

  private String lang;

  @Column(name = "last_change")
  private Timestamp lastChange;

  @OneToMany(mappedBy = "user")
  private List<Comment> comments;

  @OneToMany(mappedBy = "user1")
  private List<Friend> friends1;

  @OneToMany(mappedBy = "user2")
  private List<Friend> friends2;

  @OneToMany(mappedBy = "lastActionUserId")
  private List<Friend> friends3;

  @OneToMany(mappedBy = "user")
  private List<Post> posts;

  @OneToMany(mappedBy = "user")
  private List<UserImage> userImages;

  @Transient
  private UserImage image;

  @Version
  private Long version;

  public UserImage getImage() {
    if (this.userImages == null) {
      return null;
    }
    this.image = this.userImages.stream().filter(UserImage::getIsCurrent).findFirst()
        .orElse(null);
    if (this.image == null) { //set a default image
      UserImage manImage = new UserImage();
      manImage.setWidth(IMG_WIDTH);
      manImage.setHeight(IMG_HEIGHT);
      manImage.setName(MAN_NAME);
      UserImage womanImage = new UserImage();
      womanImage.setWidth(IMG_WIDTH);
      womanImage.setHeight(IMG_HEIGHT);
      womanImage.setName(WOMAN_NAME);
      this.image = (this.sex == 0) ? manImage : womanImage;
    }
    return image;
  }

}