package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the admin database table.
 *
 */
@Entity
//@NamedQueries({
//	@NamedQuery(name="Server.findAll", query="SELECT s FROM Server s ORDER BY s.serverID"),
//	@NamedQuery(name="Server.findById", query="SELECT s FROM Server s WHERE s.serverID = :id"),
//})
public class Server implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer serverID;

	private String serverName;

	private String serverCode;

	private String desc;

	private String ip;

	private int port;

	public Server() {
	}

	public Integer getServerID() {
		return serverID;
	}

	public void setServerID(Integer serverID) {
		this.serverID = serverID;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServerCode() {
		return serverCode;
	}

	public void setServerCode(String serverCode) {
		this.serverCode = serverCode;
	}

}
