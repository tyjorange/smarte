package com.rogy.smarte.entity.db1;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * The persistent class for the admin database table.
 * 
 */
@Entity
//@NamedQueries({
//	@NamedQuery(name="Admin.findAll", query="SELECT a FROM Admin a"),
//	@NamedQuery(name="Admin.findByUserName", query="SELECT a FROM Admin a WHERE a.username= :username"),
//	@NamedQuery(name="Admin.findById", query="SELECT a FROM Admin a WHERE a.id= :id"),
//	@NamedQuery(name="Admin.findByUsernameAndPassword", query="SELECT a FROM Admin a WHERE a.username = :username AND a.password = :password")
//})
public class Admin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String password;

	private BigInteger timeMills;

	private String username;

	public Admin() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigInteger getTimeMills() {
		return this.timeMills;
	}

	public void setTimeMills(BigInteger timeMills) {
		this.timeMills = timeMills;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}