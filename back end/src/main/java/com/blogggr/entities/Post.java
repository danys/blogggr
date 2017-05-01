package com.blogggr.entities;

import com.blogggr.json.JsonTransformer;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the posts database table.
 * 
 */
@Entity
@Table(name="posts", schema="blogggr")
public class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq",sequenceName="blogggr.posts_postid_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long postID;

	private String shortTitle;

	private String textBody;

	private Timestamp timestamp;

	private String title;

	private Boolean isGlobal;

	//bi-directional many-to-one association to Comment
	@OneToMany(mappedBy="post", fetch=FetchType.LAZY)
	private List<Comment> comments;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userid")
	private User user;

	public Post() {
	}

	public String getTimestamp(){
		return JsonTransformer.timestampToString(timestamp);
	}

	public Long getPostID() {
		return this.postID;
	}

	public void setPostID(Long postid) {
		this.postID = postid;
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

}