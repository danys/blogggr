package com.blogggr.entities;

import com.blogggr.dto.UserEnums.Lang;
import com.blogggr.dto.UserEnums.Sex;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Size;
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

  @Column(name = "password_hash")
  @Size(max=60)
  private String passwordHash;

  @Column
  @Size(max=64)
  private String challenge;

  private Integer status;

  @Enumerated(EnumType.STRING)
  private Sex sex; //m=male, f=female

  @Enumerated(EnumType.STRING)
  private Lang lang;

  @Column(name = "last_change")
  private Timestamp lastChange;

  //Additional fields that are not persisted in this table

  @OneToMany(mappedBy = "user")
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "user1")
  private List<Friend> friends1 = new ArrayList<>();

  @OneToMany(mappedBy = "user2")
  private List<Friend> friends2 = new ArrayList<>();

  @OneToMany(mappedBy = "lastActionUserId")
  private List<Friend> friends3 = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private List<Post> posts = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private List<UserImage> userImages = new ArrayList<>();

  @Transient
  private UserImage image;

  @Version
  private Long version;

  //Helper methods

  public UserImage getImage() {
    if (this.userImages.isEmpty()) {
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
      this.image = (this.sex.name().compareTo("m") == 0) ? manImage : womanImage;
    }
    return image;
  }

}