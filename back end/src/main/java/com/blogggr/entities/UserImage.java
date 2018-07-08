package com.blogggr.entities;

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
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 19.08.17.
 */
@Entity
@Table(name = "user_images", schema = "blogggr")
@Getter
@Setter
public class UserImage implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "user_image_id")
  @SequenceGenerator(name = "seqUserImage", sequenceName = "blogggr.user_image_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUserImage")
  private Long userImageId;

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
}
