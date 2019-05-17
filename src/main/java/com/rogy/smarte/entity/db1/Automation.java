package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the automation database table.
 * 
 */
@Entity
//@NamedQueries({
//	@NamedQuery(name="Automation.findAll", query="SELECT a FROM Automation a"),
//	@NamedQuery(name="Automation.findById",query="SELECT a FROM Automation a WHERE a.automationID=:id"),
//	@NamedQuery(name="Automation.findBySwitchId",query="SELECT a FROM Automation a WHERE a.switchs.switchID=:switchID")
//})

public class Automation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String automationID;

	private String name;

	//bi-directional many-to-one association to Switch
	@ManyToOne
	@JoinColumn(name="switchID")
	private Switch switchs;

	public Automation() {
	}

	public String getAutomationID() {
		return this.automationID;
	}

	public void setAutomationID(String automationID) {
		this.automationID = automationID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Switch getSwitchs() {
		return this.switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

}