package com.blogggr.entities;

import com.blogggr.json.JsonTransformer;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the comments database table.
 * 
 */
@Entity
@Table(name="comments", schema="blogggr")
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "comment_id")
	@SequenceGenerator(name="seq",sequenceName="blogggr.comment_id_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long commentID;

	private String text;

	private Timestamp timestamp;

	//bi-directional many-to-one association to Post
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="post_id")
	private Post post;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	@Version
	private Long version;

	public Comment() {
	}

	public Long getCommentID() {
		return this.commentID;
	}

	public void setCommentID(Long commentid) {
		this.commentID = commentid;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTimestamp(){
		return JsonTransformer.timestampToString(timestamp);
	}

	@JsonIgnore
	public Timestamp getRealTimestamp(){
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