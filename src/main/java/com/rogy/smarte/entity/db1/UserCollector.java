package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the user_collector database table.
 *
 */
@Entity
@Table(name="user_collector")
//@NamedQueries({
//	@NamedQuery(name="UserCollector.findAll", query="SELECT u FROM UserCollector u"),
//	@NamedQuery(name="UserCollector.findByUserID", query="SELECT u FROM UserCollector u WHERE u.user.id = :userID"),
//	@NamedQuery(name="UserCollector.findByUserIDAndCollectorID", query="SELECT u FROM UserCollector u WHERE u.user.id = :userID AND u.collector.collectorID = :collectorID"),
//	@NamedQuery(name="UserCollector.findByCollectorID", query="SELECT u FROM UserCollector u WHERE u.collector.collectorID = :collectorID"),
//	@NamedQuery(name="UserCollector.findBySetupCode", query="SELECT u FROM UserCollector u WHERE u.collector.setupCode = :setupCode"),
//	@NamedQuery(name="UserCollector.findByUsernameAndCollectorID", query="SELECT u FROM UserCollector u WHERE u.user.username = :username AND u.collector.collectorID = :collectorID")
//})
public class UserCollector implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="collectorID")
	private Collector collector;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userID")
	private User user;

	public UserCollector() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Collector getCollector() {
		return collector;
	}

	public void setCollector(Collector collector) {
		this.collector = collector;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
