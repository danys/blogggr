package com.blogggr.entities;

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
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the posts database table.
 */
@Entity
@Table(name = "posts", schema = "blogggr")
@Getter
@Setter
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

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
  private List<Comment> comments;

  @OneToMany(mappedBy = "post")
  private List<PostImage> postImages;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Version
  private Long version;
}