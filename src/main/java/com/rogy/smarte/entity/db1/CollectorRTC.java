package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "collector_rtc")
//@NamedQueries({
//        @NamedQuery(name = "CollectorRTC.findAll", query = "SELECT c FROM CollectorRTC c ORDER BY c.id DESC"),
//        @NamedQuery(name = "CollectorRTC.findByCollectorID", query = "SELECT c FROM CollectorRTC c WHERE c.collector.collectorID = :collectorID ORDER BY c.id DESC"),
//        @NamedQuery(name = "CollectorRTC.findByCollectorCode", query = "SELECT c FROM CollectorRTC c WHERE c.collector.code = :code ORDER BY c.id DESC")
//})
public class CollectorRTC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @ManyToOne
    @JoinColumn(name = "collectorID")
    private Collector collector;

    private Timestamp rtcTime;

    private Timestamp srvTime;

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

    public Timestamp getRtcTime() {
        return rtcTime;
    }

    public void setRtcTime(Timestamp rtcTime) {
        this.rtcTime = rtcTime;
    }

    public Timestamp getSrvTime() {
        return srvTime;
    }

    public void setSrvTime(Timestamp srvTime) {
        this.srvTime = srvTime;
    }

}
