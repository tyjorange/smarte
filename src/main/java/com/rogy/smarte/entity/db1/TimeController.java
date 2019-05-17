package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;


/**
 * The persistent class for the time_controller database table.
 *
 */
@Entity
@Table(name="time_controller")
//@NamedQueries({
//	@NamedQuery(name="TimeController.findAll", query="SELECT t FROM TimeController t"),
//	@NamedQuery(name="TimeController.findBySwitchID", query="SELECT t FROM TimeController t WHERE t.switchs.switchID = :switchID"),
//	@NamedQuery(name="TimeController.findByCollectorID", query="SELECT t FROM TimeController t WHERE t.switchs.collector.collectorID = :collectorID"),
//	@NamedQuery(name="TimeController.findAllEnabled", query="SELECT t FROM TimeController t WHERE t.state <> 0"),
//	@NamedQuery(name="TimeController.findAllDisabled", query="SELECT t FROM TimeController t WHERE t.state = 0"),
//	@NamedQuery(name="TimeController.findByTimeControllerID", query="SELECT t FROM TimeController t WHERE t.id = :timeControllerID"),
//	@NamedQuery(name="TimeController.findByOthers", query="SELECT t FROM TimeController t WHERE t.switchs.switchID= :switchID and t.state= :state and t.cmdData= :cmdData and t.weekday= :weekday and t.runTime = :runTime")
//})
public class TimeController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Byte cmdData;

	private Time runTime;

	private Byte state;

	private int weekday;

	private Byte upload;

	//bi-directional many-to-one association to Collector
	@ManyToOne
	@JoinColumn(name="switchID")
	private Switch switchs;


	public TimeController() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Byte getCmdData() {
		return cmdData;
	}

	public void setCmdData(Byte cmdData) {
		this.cmdData = cmdData;
	}

	public Byte getState() {
		return state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	public Byte getUpload() {
		return upload;
	}

	public void setUpload(Byte upload) {
		this.upload = upload;
	}

	public Time getRunTime() {
		return this.runTime;
	}

	public void setRunTime(Time runTime) {
		this.runTime = runTime;
	}

	public Switch getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

	public int getWeekday() {
		return this.weekday;
	}

	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{id=");
		sb.append(id);
		sb.append(" state=");
		sb.append(state);
		sb.append(" code=");
		sb.append(switchs.getCode());
		sb.append(" cmddata=");
		sb.append(cmdData);
		sb.append(" runTime=");
		sb.append(runTime.toLocalTime().toString());
		sb.append(" weekday=");
		sb.append(weekday);
		sb.append(" upload=");
		sb.append(upload);
		sb.append("}");
		return sb.toString();
	}

}
