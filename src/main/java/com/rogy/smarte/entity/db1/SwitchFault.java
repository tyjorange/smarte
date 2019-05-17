package com.rogy.smarte.entity.db1;/*package model;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


*//**
 * The persistent class for the switch_fault database table.
 * 
 *//*
@Entity
@Table(name="switch_fault")
@NamedQueries({
	@NamedQuery(name="SwitchFault.findAll", query="SELECT f FROM SwitchFault f"),
	@NamedQuery(name="SwitchFault.findById",query="SELECT f FROM SwitchFault f WHERE f.id=:id"),
	@NamedQuery(name="SwitchFault.findBySwitchId",query="SELECT f FROM SwitchFault f WHERE f.switchs.switchID=:switchID")
})
public class SwitchFault implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private int code;

	private Timestamp genTime;

	private Timestamp returnTime;

	private int state;

	@ManyToOne
	@JoinColumn(name="switchID")
	private Switch switchs;

	public SwitchFault() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Timestamp getGenTime() {
		return this.genTime;
	}

	public void setGenTime(Timestamp genTime) {
		this.genTime = genTime;
	}

	public Timestamp getReturnTime() {
		return this.returnTime;
	}

	public void setReturnTime(Timestamp returnTime) {
		this.returnTime = returnTime;
	}

	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Switch getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

}*/