package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="collector_traffic")
//@NamedQueries({
//	@NamedQuery(name="CollectorTraffic.findAll", query="SELECT c FROM CollectorTraffic c")
//})
public class CollectorTraffic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = 0L;

	@ManyToOne
	@JoinColumn(name = "collectorID")
	private Collector collector;

	private Timestamp rwtime;
	
	private Long byteread;
	
	private Long bytewrite;
	
	private Integer packetread;
	
	private Integer packetwrite;
	
	private Integer packetreaderr;
	
	private Long duration;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collector getCollector() {
		return collector;
	}

	public void setCollector(Collector collector) {
		this.collector = collector;
	}

	public Timestamp getRwtime() {
		return rwtime;
	}

	public void setRwtime(Timestamp rwtime) {
		this.rwtime = rwtime;
	}

	public Long getByteread() {
		return byteread;
	}

	public void setByteread(Long byteread) {
		this.byteread = byteread;
	}

	public Long getBytewrite() {
		return bytewrite;
	}

	public void setBytewrite(Long bytewrite) {
		this.bytewrite = bytewrite;
	}

	public Integer getPacketread() {
		return packetread;
	}

	public void setPacketread(Integer packetread) {
		this.packetread = packetread;
	}

	public Integer getPacketwrite() {
		return packetwrite;
	}

	public void setPacketwrite(Integer packetwrite) {
		this.packetwrite = packetwrite;
	}

	public Integer getPacketreaderr() {
		return packetreaderr;
	}

	public void setPacketreaderr(Integer packetreaderr) {
		this.packetreaderr = packetreaderr;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

}
