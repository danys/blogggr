package com.blogggr.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the sessions database table.
 * 
 */
@Entity
@Table(name="sessions", schema="blogggr")
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq",sequenceName="blogggr.sessions_sessionid_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long sessionID;

	private Boolean valid;

	@Column(columnDefinition = "bpchar(64)")
	private String sessionHash;

	private Timestamp validTill;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userid")
	private User user;

	public Session() {
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Long getSessionid() {
		return this.sessionID;
	}

	public void setSessionid(Long sessionid) {
		this.sessionID = sessionid;
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