package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * The persistent class for the switch database table.
 */
@Entity
//@NamedQueries({
//	@NamedQuery(name="Switch.findAll", query="SELECT s FROM Switch s"),
//	@NamedQuery(name="Switch.findBySwitchID", query="SELECT s FROM Switch s WHERE s.switchID = :switchID"),
//	@NamedQuery(name="Switch.findByCode", query="SELECT s FROM Switch s WHERE s.code = :code"),
//	@NamedQuery(name="Switch.findByCollectorID", query="SELECT s FROM Switch s WHERE s.collector.collectorID = :collectorID"),
//	@NamedQuery(name="Switch.findByCollectorIDOBCode", query="SELECT s FROM Switch s WHERE s.collector.collectorID = :collectorID ORDER BY s.code"),
//	@NamedQuery(name="Switch.findByCollectorCode", query="SELECT s FROM Switch s WHERE s.collector.code = :code"),
//	@NamedQuery(name="Switch.findByCollectorCodeOBCode", query="SELECT s FROM Switch s WHERE s.collector.code = :code ORDER BY s.code")
//})
public class Switch implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer switchID;

    private String code;

    private double frequency;

    private String name;

    private int iconType;

    private int state = 1;

    private int isLocked;

    private String lockedPwd;

    private Timestamp addTime = Timestamp.valueOf(LocalDateTime.now());

    private Integer sequence = 0;

    private int fault = 0;

    private Timestamp faultTime;

    private int faultState;

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    //bi-directional many-to-one association to Collector
    @ManyToOne
    @JoinColumn(name = "collectorID")
    private Collector collector;

    @ManyToOne
    @JoinColumn(name = "lockedUser")
    private User lockedUser;

    public int getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public String getLockedPwd() {
        return lockedPwd;
    }

    public void setLockedPwd(String lockedPwd) {
        this.lockedPwd = lockedPwd;
    }

    public User getLockedUser() {
        return lockedUser;
    }

    public void setLockedUser(User lockedUser) {
        this.lockedUser = lockedUser;
    }

    public Switch() {
    }

    public Integer getSwitchID() {
        return switchID;
    }

    public void setSwitchID(Integer switchID) {
        this.switchID = switchID;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getFrequency() {
        return this.frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collector getCollector() {
        return this.collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
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

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getFault() {
        return fault;
    }

    public void setFault(int fault) {
        this.fault = fault;
    }

    public Timestamp getFaultTime() {
        return faultTime;
    }

    public void setFaultTime(Timestamp faultTime) {
        this.faultTime = faultTime;
    }

    public int getFaultState() {
        return faultState;
    }

    public void setFaultState(int faultState) {
        this.faultState = faultState;
    }

}
