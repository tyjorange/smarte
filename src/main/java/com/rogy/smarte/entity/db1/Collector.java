package com.rogy.smarte.entity.db1;

import com.rogy.smarte.util.Code;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the collector database table.
 */
@Entity
//@NamedQueries({
//	@NamedQuery(name="Collector.findAll", query="SELECT c FROM Collector c"),
//	@NamedQuery(name="Collector.findByFsuID", query="SELECT c FROM Collector c WHERE c.fsu.fsuid = :fsuid"),
//	@NamedQuery(name="Collector.findBySetupCode", query="SELECT c FROM Collector c WHERE c.setupCode = :setupCode"),
//	@NamedQuery(name="Collector.findByCollectorCode", query="SELECT c FROM Collector c WHERE c.code = :collectorCode"),
//	@NamedQuery(name="Collector.findByCollectorID", query="SELECT c FROM Collector c WHERE c.collectorID = :collectorID"),
//	@NamedQuery(name="Collector.findByFsuCode", query="SELECT c FROM Collector c WHERE c.fsu.FSUCode = :code")
//})
public class Collector implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer collectorID;

    private String code;

    private String name;

    private int baud;

    private int verMajor = 0;
    private int verMinor = 0;

    private int ioType = 0;

    private int freq = 10;

    private int ranges = 10;

    private int HBFreq;

//	private Timestamp HBTime;

	private int active = 0;

	private Timestamp activeTime;

	private String ip;

	private String setupCode;

	//bi-directional many-to-one association to Fsu
	@ManyToOne
	@JoinColumn(name="serverID")
	private Server server;

//	private String srvIP = "116.62.38.203";

//	private int srvPort = 55555;

	private int faultState = 0;

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	private Timestamp faultTime;

    public String getSetupCode() {
        return setupCode;
    }

    public void setSetupCode(String setupCode) {
        this.setupCode = setupCode;
    }

    //bi-directional many-to-one association to Fsu
    @ManyToOne
    @JoinColumn(name = "fsuID")
    private Fsu fsu;

    public Collector() {
    }

    public Integer getCollectorID() {
        return this.collectorID;
    }

    public void setCollectorID(Integer collectorID) {
        this.collectorID = collectorID;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
        setupCode = Code.getDecSetupCode(code);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fsu getFsu() {
        return this.fsu;
    }

    public void setFsu(Fsu fsu) {
        this.fsu = fsu;
    }

    public int getBaud() {
        return baud;
    }

    public void setBaud(int baud) {
        this.baud = baud;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getRanges() {
        return ranges;
    }

    public int getFaultState() {
        return faultState;
    }

    public void setFaultState(int faultState) {
        this.faultState = faultState;
    }

    public Timestamp getFaultTime() {
        return faultTime;
    }

    public void setFaultTime(Timestamp faultTime) {
        this.faultTime = faultTime;
    }

    public void setRanges(int ranges) {
        this.ranges = ranges;
    }

    public int getHBFreq() {
        return HBFreq;
    }

    public void setHBFreq(int hBFreq) {
        HBFreq = hBFreq;
    }

//	public Timestamp getHBTime() {
//		return HBTime;
//	}
//
//	public void setHBTime(Timestamp hBTime) {
//		HBTime = hBTime;
//	}

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

	public int getVerMajor() {
		return verMajor;
	}

    public void setVerMajor(int verMajor) {
        this.verMajor = verMajor;
    }

    public int getVerMinor() {
        return verMinor;
    }

    public void setVerMinor(int verMinor) {
        this.verMinor = verMinor;
    }

    public int getIoType() {
        return ioType;
    }

    public void setIoType(int ioType) {
        this.ioType = ioType;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Timestamp getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Timestamp activeTime) {
        this.activeTime = activeTime;
    }

}
