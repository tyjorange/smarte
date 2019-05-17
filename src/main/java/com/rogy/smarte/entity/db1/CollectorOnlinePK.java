package com.rogy.smarte.entity.db1;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;

/**
 * The primary key class for the collector_online database table.
 * 
 */
@Embeddable
public class CollectorOnlinePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date ontime;

	public CollectorOnlinePK() {
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public java.util.Date getOntime() {
		return this.ontime;
	}
	public void setOntime(java.util.Date ontime) {
		this.ontime = ontime;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CollectorOnlinePK)) {
			return false;
		}
		CollectorOnlinePK castOther = (CollectorOnlinePK)other;
		return 
			this.id.equals(castOther.id)
			&& this.ontime.equals(castOther.ontime);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.id.hashCode();
		hash = hash * prime + this.ontime.hashCode();
		
		return hash;
	}
}