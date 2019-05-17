package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the signal_hour database table.
 */
@Entity
@Table(name = "signal_hour")
//@NamedQueries({
//        @NamedQuery(name="SignalHour.findAll", query="SELECT s FROM SignalHour s"),
//        @NamedQuery(name="SignalHour.findBySwitchAndTypeAndDay", query="SELECT s FROM SignalHour s WHERE s.switchs.switchID = :switchID AND s.signalsType.signalsTypeID = :signalsTypeID AND s.time = :time ORDER BY s.hour ASC"),
//        @NamedQuery(name="SignalHour.findBySwitchAndTypeAndHour", query="SELECT s FROM SignalHour s WHERE s.switchs.switchID = :switchID AND s.signalsType.signalsTypeID = :signalsTypeID AND s.time = :time AND s.hour = :hour")
//})
public class SignalHour implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    private int hour;

    @ManyToOne
    @JoinColumn(name = "signalsTypeID")
    private Signalstype signalsType;

    private double statistik;

    @ManyToOne
    @JoinColumn(name = "switchID")
    private Switch switchs;

    @Temporal(TemporalType.DATE)
    private Date time;

    private double value;

    public SignalHour() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public double getStatistik() {
        return this.statistik;
    }

    public void setStatistik(double statistik) {
        this.statistik = statistik;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Signalstype getSignalsType() {
        return signalsType;
    }

    public void setSignalsType(Signalstype signalsType) {
        this.signalsType = signalsType;
    }

    public Switch getSwitchs() {
        return switchs;
    }

    public void setSwitchs(Switch switchs) {
        this.switchs = switchs;
    }

}
