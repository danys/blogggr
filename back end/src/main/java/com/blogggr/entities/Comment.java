package com.blogggr.entities;

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
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the comments database table.
 */
@Entity
@Table(name = "comments", schema = "blogggr")
@Getter
@Setter
public class Comment implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "comment_id")
  @SequenceGenerator(name = "seqComment", sequenceName = "blogggr.comment_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqComment")
  private Long commentId;

  private String text;

  private Timestamp timestamp;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Version
  private Long version;
}