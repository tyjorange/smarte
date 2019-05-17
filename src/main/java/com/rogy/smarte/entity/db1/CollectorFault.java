package com.rogy.smarte.entity.db1;/*package model;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


*//**
 * The persistent class for the collector_fault database table.
 * 
 *//*
@Entity
@Table(name="collector_fault")
@NamedQueries({
	@NamedQuery(name="CollectorFault.findAll", query="SELECT c FROM CollectorFault c"),
	@NamedQuery(name="CollectorFault.findById",query="SELECT c FROM CollectorFault c WHERE c.id=:id"),
	@NamedQuery(name="CollectorFault.findByCollectorId",query="SELECT c FROM CollectorFault c WHERE c.collector.collectorID=:collectorID")
})
public class CollectorFault implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private int code;

	@ManyToOne
	@JoinColumn(name="collectorID")
	private Collector collector;

	private Timestamp genTime;

	private Timestamp returnTime;

	private int state;

	public CollectorFault() {
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

	public Collector getCollector() {
		return collector;
	}

	public void setCollector(Collector collector) {
		this.collector = collector;
	}

}*/