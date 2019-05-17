package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="collector_timerupload")
//@NamedQueries({
//		@NamedQuery(name = "CollectorTimerUpload.findAll", query = "SELECT c FROM CollectorTimerUpload c ORDER BY c.id DESC"),
//		@NamedQuery(name = "CollectorTimerUpload.findByCollectorID", query = "SELECT c FROM CollectorTimerUpload c WHERE c.collector.collectorID = :collectorID ORDER BY c.id DESC"),
//		@NamedQuery(name = "CollectorTimerUpload.findByCollectorCodeAndMsg", query = "SELECT c FROM CollectorTimerUpload c WHERE c.collector.code = :code AND c.msgId = :msgId AND c.msgNo = :msgNo ORDER BY c.id DESC"),
//		@NamedQuery(name = "CollectorTimerUpload.findNoResultBefore", query = "SELECT c FROM CollectorTimerUpload c WHERE c.resultTime IS NULL AND c.uploadTime <= :beforeTime ORDER BY c.id")
//})
public class CollectorTimerUpload {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = 0L;

	@ManyToOne
	@JoinColumn(name = "collectorID")
	private Collector collector;
	
	private Timestamp uploadTime;
	
	private Integer msgId;
	
	private Integer msgNo;

	private Timestamp resultTime;
	
	private Integer result;
	
	private int fail = 0;

	public Collector getCollector() {
		return collector;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCollector(Collector collector) {
		this.collector = collector;
	}

	public Timestamp getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Timestamp uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}

	public Integer getMsgNo() {
		return msgNo;
	}

	public void setMsgNo(Integer msgNo) {
		this.msgNo = msgNo;
	}

	public Timestamp getResultTime() {
		return resultTime;
	}

	public void setResultTime(Timestamp resultTime) {
		this.resultTime = resultTime;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public int getFail() {
		return fail;
	}

	public void setFail(int fail) {
		this.fail = fail;
	}
}
