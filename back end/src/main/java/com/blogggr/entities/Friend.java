package com.blogggr.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the friends database table.
 * 
 */
@Entity
@Table(name="friends", schema="blogggr")
public class Friend implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FriendPK id;

	/**
	 * Status meaning:
	 * 0 = pending
	 * 1 = accepted
	 * 2 = declined
	 * 3 = blocked
	 */
	private Integer status;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="useroneid")
	private User user1;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="usertwoid")
	private User user2;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="actionuserid")
	private User user3; //user that most recently changed the status column

	public Friend() {
	}

	public FriendPK getId() {
		return this.id;
	}

	public void setId(FriendPK id) {
		this.id = id;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public User getUser1() {
		return this.user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return this.user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	public User getUser3() {
		return this.user3;
	}

	public void setUser3(User user3) {
		this.user3 = user3;
	}

}