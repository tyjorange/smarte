package com.rogy.smarte.entity.db1;

import javax.persistence.Entity;
import javax.persistence.Id;


/**
 *
 */
@Entity
public class SwitchWithTimer {
//	SELECT s.switchID, s.code, s.name, s.iconType, s.state, s.sequence, s.fault, s.faultState, count(tc.id)
//	FROM Switch s left join Time_Controller tc on  s.switchID = tc.switchID
//	WHERE s.collectorID = 'D0B487F43EFB4C3B9B988D2246B57A95'
//	group by s.switchID;

	@Id
	private Integer switchID;
	private String code;
	private String name;
	private int iconType;
	private int state;
	private Integer sequence;
	private int fault;
	private int faultState;
	private int timerCount;

	public Integer getSwitchID() {
		return switchID;
	}
	public void setSwitchID(Integer switchID) {
		this.switchID = switchID;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIconType() {
		return iconType;
	}
	public void setIconType(int iconType) {
		this.iconType = iconType;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public int getFault() {
		return fault;
	}
	public void setFault(int fault) {
		this.fault = fault;
	}
	public int getFaultState() {
		return faultState;
	}
	public void setFaultState(int faultState) {
		this.faultState = faultState;
	}
	public int getTimerCount() {
		return timerCount;
	}
	public void setTimerCount(int timerCount) {
		this.timerCount = timerCount;
	}
}
