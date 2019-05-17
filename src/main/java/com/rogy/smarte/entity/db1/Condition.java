package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the conditions database table.
 */
@Entity
@Table(name = "conditions")
//@NamedQueries({
//        @NamedQuery(name = "Condition.findAll", query = "SELECT c FROM Condition c"),
//        @NamedQuery(name = "Condition.findById", query = "SELECT c FROM Condition c WHERE c.conditionsID=:id"),
//        @NamedQuery(name = "Condition.findConByAtmID", query = "SELECT c FROM Condition c WHERE c.automation.automationID=:id")
//})
public class Condition implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String conditionsID;

    private String threshold;

    //bi-directional many-to-one association to Signalstype
    @ManyToOne
    @JoinColumn(name = "signalsTypeID")
    private Signalstype signalstype;

    //bi-directional many-to-one association to Automation
    @ManyToOne
    @JoinColumn(name = "automationID")
    private Automation automation;

    public Condition() {
    }

    public String getConditionsID() {
        return this.conditionsID;
    }

    public void setConditionsID(String conditionsID) {
        this.conditionsID = conditionsID;
    }

    public String getThreshold() {
        return this.threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public Signalstype getSignalstype() {
        return this.signalstype;
    }

    public void setSignalstype(Signalstype signalstype) {
        this.signalstype = signalstype;
    }

    public Automation getAutomation() {
        return this.automation;
    }

    public void setAutomation(Automation automation) {
        this.automation = automation;
    }

}