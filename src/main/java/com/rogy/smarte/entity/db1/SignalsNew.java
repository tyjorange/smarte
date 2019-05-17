package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the signals_new database table.
 *
 */
@Entity
@Table(name="signals_new")
//@NamedQueries({
//	@NamedQuery(name="SignalsNew.findAll", query="SELECT s FROM SignalsNew s"),
//	@NamedQuery(name="SignalsNew.findSignalsNewWithDL", query="SELECT s FROM SignalsNew s WHERE s.signalstype.signalsTypeID = :signalsTypeID AND s.value > :value"),
//	@NamedQuery(name="SignalsNew.findBySwitchIDAndSignalsTypeID", query="SELECT s FROM SignalsNew s WHERE s.switchs.switchID = :switchID AND s.signalstype.signalsTypeID = :signalsTypeID"),
//	@NamedQuery(name="SignalsNew.findBySwitchAndSignalsType",  query="SELECT sn FROM SignalsNew sn, Signalstype st WHERE sn.switchs.switchID = :switchID AND sn.signalstype.signalsTypeID = st.signalsTypeID")
//})
public class SignalsNew implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	//bi-directional many-to-one association to Signalstype
	@ManyToOne
	@JoinColumn(name="signalsTypeID")
	private Signalstype signalstype;

	//bi-directional many-to-one association to Switch
	@ManyToOne
	@JoinColumn(name="switchID")
	private Switch switchs;

	private Timestamp time;

	private double value;

	public SignalsNew() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Signalstype getSignalstype() {
		return signalstype;
	}

	public void setSignalstype(Signalstype signalstype) {
		this.signalstype = signalstype;
	}

	public Switch getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

	public Timestamp getTime() {
		return this.time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
