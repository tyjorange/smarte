package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the apex_day database table.
 * 
 */
@Entity
@Table(name = "apex_day")
//@NamedQueries({
//		@NamedQuery(name = "ApexDay.findAll", query = "SELECT a FROM ApexDay a"),
//		@NamedQuery(name = "ApexDay.findBySwitchAndSignalsTypeAndTime", query = "SELECT a FROM ApexDay a WHERE a.switchs.switchID = :switchID AND a.signalsType.signalsTypeID = :signalsTypeID AND a.time = :time") })
public class ApexDay implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = 0L;

	private Double maxDay;

	private Double minDay;

	// private String signalsTypeID;

	// private String switchID;

	@ManyToOne
	@JoinColumn(name = "signalsTypeID")
	private Signalstype signalsType;

	@ManyToOne
	@JoinColumn(name = "switchID")
	private Switch switchs;

	@Temporal(TemporalType.DATE)
	private Date time;

	public ApexDay() {
	}

	public Double getMaxDay() {
		return maxDay;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMaxDay(Double maxDay) {
		this.maxDay = maxDay;
	}

	public Double getMinDay() {
		return minDay;
	}

	public void setMinDay(Double minDay) {
		this.minDay = minDay;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}