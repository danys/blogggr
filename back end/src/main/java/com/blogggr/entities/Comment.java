package com.blogggr.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the comments database table.
 * 
 */
@Entity
@Table(name="comments")
@NamedQuery(name="Comment.findAll", query="SELECT c FROM Comment c")
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="COMMENTS_COMMENTID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="COMMENTS_COMMENTID_GENERATOR")
	private Long commentID;

	private String text;

	private Timestamp timestamp;

	//bi-directional many-to-one association to Post
	@ManyToOne
	@JoinColumn(name="postid")
	private Post post;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userid")
	private User user;

	public Comment() {
	}

	public Long getCommentid() {
		return this.commentID;
	}

	public void setCommentid(Long commentid) {
		this.commentID = commentid;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
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

}