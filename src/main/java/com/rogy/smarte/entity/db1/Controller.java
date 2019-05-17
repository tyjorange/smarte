package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the controller database table.
 */
@Entity
//@NamedQueries({
//        @NamedQuery(name = "Controller.findAll", query = "SELECT c FROM Controller c"),
//        @NamedQuery(name = "Controller.findByControllerID", query = "SELECT c FROM Controller c WHERE c.controllerID = :controllerID"),
//        @NamedQuery(name = "Controller.findNotRun", query = "SELECT c FROM Controller c WHERE c.runCode = NULL order by c.genTime"),
//        @NamedQuery(name = "Controller.findRun", query = "SELECT c FROM Controller c WHERE c.runId = :runId AND c.runNo = :runNo")
//})
public class Controller implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long controllerID;

	@ManyToOne
	@JoinColumn(name = "userID")
	private User user;

	private Byte cmdData;

    private Timestamp genTime;

    private String runCode;

	private Timestamp runTime;

	private Integer runId;

	private Integer runNo;

	private Integer runResult;

	private Byte source;

	private Integer targetID;

	private Byte targetType;

    public Controller() {
    }

	public Long getControllerID() {
		return controllerID;
	}

	public void setControllerID(Long controllerID) {
		this.controllerID = controllerID;
	}

	public Byte getCmdData() {
		return cmdData;
	}

	public void setCmdData(Byte cmdData) {
		this.cmdData = cmdData;
	}

	public Byte getSource() {
		return source;
	}

	public void setSource(Byte source) {
		this.source = source;
	}

	public Byte getTargetType() {
		return targetType;
	}

	public void setTargetType(Byte targetType) {
		this.targetType = targetType;
	}

	public User getUser() {
		return user;
	}

    public void setUser(User user) {
        this.user = user;
    }

	public Timestamp getGenTime() {
		return this.genTime;
	}

    public void setGenTime(Timestamp genTime) {
        this.genTime = genTime;
    }

    public String getRunCode() {
        return this.runCode;
    }

    public void setRunCode(String runCode) {
        this.runCode = runCode;
    }

    public Timestamp getRunTime() {
        return this.runTime;
    }

    public void setRunTime(Timestamp runTime) {
        this.runTime = runTime;
    }

    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    public Integer getRunNo() {
        return runNo;
    }

    public void setRunNo(Integer runNo) {
        this.runNo = runNo;
    }

    public Integer getRunResult() {
        return runResult;
    }

    public void setRunResult(Integer runResult) {
        this.runResult = runResult;
    }

	public Integer getTargetID() {
		return targetID;
	}

	public void setTargetID(Integer targetID) {
		this.targetID = targetID;
	}


}
