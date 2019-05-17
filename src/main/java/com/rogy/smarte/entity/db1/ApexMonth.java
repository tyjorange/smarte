package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the apex_month database table.
 * 
 */
@Entity
@Table(name = "apex_month")
//@NamedQueries({
//		@NamedQuery(name = "ApexMonth.findAll", query = "SELECT a FROM ApexMonth a"),
//		@NamedQuery(name = "ApexMonth.findBySwitchAndSignalsTypeAndTime", query = "SELECT a FROM ApexMonth a WHERE a.switchs.switchID = :switchID AND a.signalsType.signalsTypeID = :signalsTypeID AND a.timeYear = :timeYear AND a.timeMonth = :timeMonth") })
public class ApexMonth implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = 0L;

	private double maxMonth;

	private double minMonth;

	@ManyToOne
	@JoinColumn(name = "signalsTypeID")
	private Signalstype signalsType;

	@ManyToOne
	@JoinColumn(name = "switchID")
	private Switch switchs;

	@Column(name = "time_month")
	private int timeMonth;

	@Column(name = "time_year")
	private int timeYear;

	public ApexMonth() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getMaxMonth() {
		return maxMonth;
	}

	public void setMaxMonth(double maxMonth) {
		this.maxMonth = maxMonth;
	}

	public double getMinMonth() {
		return minMonth;
	}

	public void setMinMonth(double minMonth) {
		this.minMonth = minMonth;
	}

	public Signalstype getSignalsType() {
		return signalsType;
	}

	public void setSignalsType(Signalstype signalsType) {
		this.signalsType = signalsType;
	}

	public Switch getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

	public int getTimeMonth() {
		return this.timeMonth;
	}

	public void setTimeMonth(int timeMonth) {
		this.timeMonth = timeMonth;
	}

	public int getTimeYear() {
		return this.timeYear;
	}

	public void setTimeYear(int timeYear) {
		this.timeYear = timeYear;
	}

}