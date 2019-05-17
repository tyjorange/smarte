package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the switch_param database table.
 * 
 */
@Entity
@Table(name="switch_param")
//@NamedQueries({
//	@NamedQuery(name="SwitchParam.findAll", query="SELECT s FROM SwitchParam s"),
//	@NamedQuery(name="SwitchParam.findByID", query="SELECT s FROM SwitchParam s WHERE s.id = :id"),
//	@NamedQuery(name="SwitchParam.findBySwitchIDParamID", query="SELECT s FROM SwitchParam s WHERE s.switchs.switchID = :switchID AND s.paramID = :paramID")
//})
public class SwitchParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Integer paramID;

	private String paramValue;
	
	private Timestamp genTime;
	
	private int state;
	
	private Timestamp returnTime;

	//bi-directional many-to-one association to switch
	@ManyToOne
	@JoinColumn(name="switchID")
	private Switch switchs;

	public SwitchParam() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getParamID() {
		return this.paramID;
	}

	public void setParamID(Integer paramID) {
		this.paramID = paramID;
	}

	public String getParamValue() {
		return this.paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public Switch getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

	public Timestamp getGenTime() {
		return genTime;
	}

	public void setGenTime(Timestamp genTime) {
		this.genTime = genTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Timestamp getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(Timestamp returnTime) {
		this.returnTime = returnTime;
	}
}