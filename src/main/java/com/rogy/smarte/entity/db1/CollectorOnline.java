package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the collector_online database table.
 */
@Entity
@Table(name = "collector_online")
//@NamedQuery(name="CollectorOnline.findAll", query="SELECT c FROM CollectorOnline c")
public class CollectorOnline implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7442115231349515691L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @ManyToOne
    @JoinColumn(name = "collectorID")
    private Collector collector;

    private Timestamp ontime;

    private byte onstatus;

    private int reason;

    public CollectorOnline() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte getOnstatus() {
        return this.onstatus;
    }

    public void setOnstatus(byte onstatus) {
        this.onstatus = onstatus;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public Timestamp getOntime() {
        return ontime;
    }

    public void setOntime(Timestamp ontime) {
        this.ontime = ontime;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

}