package com.blogggr.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the friends database table.
 * 
 */
@Embeddable
public class FriendPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=false, updatable=false)
	private Long userOneID;

	@Column(insertable=false, updatable=false)
	private Long userTwoID;

	public FriendPK() {
	}
	public Long getUserOneID() {
		return this.userOneID;
	}
	public void setuserOneID(Long useroneid) {
		this.userOneID = userOneID;
	}
	public Long getuserTwoID() {
		return this.userTwoID;
	}
	public void setuserTwoID(Long usertwoid) {
		this.userTwoID = usertwoid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FriendPK)) {
			return false;
		}
		FriendPK castOther = (FriendPK)other;
		return 
			this.userOneID.equals(castOther.userOneID)
			&& this.userTwoID.equals(castOther.userTwoID);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userOneID.hashCode();
		hash = hash * prime + this.userTwoID.hashCode();
		
		return hash;
	}
}