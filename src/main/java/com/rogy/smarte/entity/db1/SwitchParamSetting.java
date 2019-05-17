package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The persistent class for the switch_param_setting database table.
 * 
 */
@Entity
@Table(name="switch_param_setting")
//@NamedQueries({
//	@NamedQuery(name="SwitchParamSetting.findAll", query="SELECT s FROM SwitchParamSetting s"),
//	@NamedQuery(name="SwitchParamSetting.findByID", query="SELECT s FROM SwitchParamSetting s WHERE s.id = :id")
//})
public class SwitchParamSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = 0L;

	@ManyToOne
	@JoinColumn(name="switchID")
	private Switch switchs;
	
	private Integer paramID;

	private String paramValue;
	
	private Timestamp time;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Switch getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

	public Integer getParamID() {
		return paramID;
	}

	public void setParamID(Integer paramID) {
		this.paramID = paramID;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}
}
