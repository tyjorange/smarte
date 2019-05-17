package com.rogy.smarte.entity.db1;

import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="zdata")
public class ZData {

	@Id
	private Long id;
	private Integer switchID;
	private Short signalsTypeID;
	private int year;
	private Time time;
	private Double value;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getSwitchID() {
		return switchID;
	}
	public void setSwitchID(Integer switchID) {
		this.switchID = switchID;
	}
	public Short getSignalsTypeID() {
		return signalsTypeID;
	}
	public void setSignalsTypeID(Short signalsTypeID) {
		this.signalsTypeID = signalsTypeID;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Time getTime() {
		return time;
	}
	public void setTime(Time time) {
		this.time = time;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}


}
