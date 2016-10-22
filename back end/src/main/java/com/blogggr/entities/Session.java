package com.blogggr.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the sessions database table.
 * 
 */
@Entity
@Table(name="sessions")
@NamedQuery(name="Session.findAll", query="SELECT s FROM Session s")
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SESSIONS_SESSIONID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SESSIONS_SESSIONID_GENERATOR")
	private Long sessionID;

	private Timestamp lastActionTime;

	private String sessionHash;

	private Timestamp validTill;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userid")
	private User user;

	public Session() {
	}

	public Long getSessionid() {
		return this.sessionID;
	}

	public void setSessionid(Long sessionid) {
		this.sessionID = sessionid;
	}

	public Timestamp getLastActionTime() {
		return this.lastActionTime;
	}

	public void setLastActionTime(Timestamp lastactiontime) {
		this.lastActionTime = lastactiontime;
	}

	public String getSessionhash() {
		return this.sessionHash;
	}

	public void setSessionhash(String sessionhash) {
		this.sessionHash = sessionhash;
	}

	public Timestamp getValidtill() {
		return this.validTill;
	}

	public void setValidtill(Timestamp validtill) {
		this.validTill = validtill;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}