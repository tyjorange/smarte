package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the alarm database table.
 *
 */
@Entity
//@NamedQuery(name="Alarm.findAll", query="SELECT a FROM Alarm a")
public class Alarm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	//bi-directional many-to-one association to Switch
	@ManyToOne
	@JoinColumn(name="collectorID")
	private Collector collector;

	private String desc;

	//bi-directional many-to-one association to Switch
	@ManyToOne
	@JoinColumn(name="switchID")
	private Switch switchs;

	private Timestamp time;

	private int type;

	public Alarm() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Timestamp getTime() {
		return this.time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Collector getCollector() {
		return collector;
	}

	public void setCollector(Collector collector) {
		this.collector = collector;
	}

	public Switch getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

}
