package com.rogy.smarte.fsu;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 断路器控制命令对象。
 * 包含要对断路器执行的操作及对应的数据库记录、执行时间、执行结果等等。
 */
public class BreakerCommand {
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0L);	// ID生成器。

	private long id;			// ID
	private long codeValueBreaker;		// 断路器地址数值。
	private int breakerId;				// 断路器记录ID。
	private long codeValueCollector;		// 集中器地址数值。
	private int collectorId;			// 集中器记录ID。
	private int commandData;				// 命令数据。0=关;1=开。
	private long collectorInfoId;			// 对应的CollectorInfo的ID。
	private long controllerId;			// 对应的Controller记录的ID。
	private LocalDateTime requestTime;	// 命令请求执行时间。
	private LocalDateTime submitTime;	// 提交执行时间。
	private LocalDateTime runTime;		// 执行开始时间。
	private LocalDateTime overTime;		// 执行完毕时间。
	private volatile int errCode;			// 错误码。

	public BreakerCommand() {
		id = ID_GENERATOR.getAndIncrement();
		collectorInfoId = -1;
	}
	public long getId() {
		return id;
	}
	public long getCodeValueBreaker() {
		return codeValueBreaker;
	}
	public long getCollectorInfoId() {
		return collectorInfoId;
	}
	public void setCollectorInfoId(long collectorInfoId) {
		this.collectorInfoId = collectorInfoId;
	}
	public void setCodeValueBreaker(long codeValueBreaker) {
		this.codeValueBreaker = codeValueBreaker;
	}
	public long getCodeValueCollector() {
		return codeValueCollector;
	}
	public void setCodeValueCollector(long codeValueCollector) {
		this.codeValueCollector = codeValueCollector;
	}
	public LocalDateTime getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(LocalDateTime requestTime) {
		this.requestTime = requestTime;
	}
	public LocalDateTime getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(LocalDateTime submitTime) {
		this.submitTime = submitTime;
	}
	public LocalDateTime getOverTime() {
		return overTime;
	}
	public void setOverTime(LocalDateTime overTime) {
		this.overTime = overTime;
	}
	public int getCommandData() {
		return commandData;
	}
	public void setCommandData(int commandData) {
		this.commandData = commandData;
	}
	public LocalDateTime getRunTime() {
		return runTime;
	}
	public void setRunTime(LocalDateTime runTime) {
		this.runTime = runTime;
	}
	public int getErrCode() {
		return errCode;
	}
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public int getBreakerId() {
		return breakerId;
	}
	public void setBreakerId(int breakerId) {
		this.breakerId = breakerId;
	}
	public int getCollectorId() {
		return collectorId;
	}
	public void setCollectorId(int collectorId) {
		this.collectorId = collectorId;
	}
	public long getControllerId() {
		return controllerId;
	}
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ID=");
		sb.append(id);
		sb.append(" BreakerID=");
		sb.append(breakerId);
		sb.append(" BreakerCode=");
		sb.append(String.format("%012X", codeValueBreaker));
		sb.append(" CollectorID=");
		sb.append(collectorId);
		sb.append(" collectorCode=");
		sb.append(String.format("%012X", codeValueCollector));
		sb.append(" CollectorInfoID=");
		sb.append(collectorInfoId);
		sb.append(" ControllerID=");
		sb.append(controllerId);
		sb.append(" CommandData=");
		sb.append(commandData);
		sb.append(" RequestTime=");
		sb.append(requestTime);
		sb.append(" SubmitTime=");
		sb.append(submitTime);
		sb.append(" RunTime=");
		sb.append(runTime);
		sb.append(" OverTime=");
		sb.append(overTime);
		sb.append(" ErrCode=");
		sb.append(errCode);
		sb.append("}");
		return sb.toString();
	}
}
