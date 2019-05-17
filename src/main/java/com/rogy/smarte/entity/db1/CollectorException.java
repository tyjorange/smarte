package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "collector_exception")
//@NamedQueries({
//        @NamedQuery(name = "CollectorException.findAll", query = "SELECT c FROM CollectorException c ORDER BY c.excpdate"),
//        @NamedQuery(name = "CollectorException.findAllByDate", query = "SELECT c FROM CollectorException c WHERE c.excpdate = :excpdate ORDER BY c.collector.code"),
//        @NamedQuery(name = "CollectorException.findByCollectIDAndDate", query = "SELECT c FROM CollectorException c WHERE c.collector.collectorID = :collectorID AND c.excpdate = :excpdate")
//})
public class CollectorException {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @ManyToOne
    @JoinColumn(name = "collectorID")
    private Collector collector;

    @Temporal(TemporalType.DATE)
    private Date excpdate;

    private int excpcount;

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

    public Date getExcpdate() {
        return excpdate;
    }

    public void setExcpdate(Date excpdate) {
        this.excpdate = excpdate;
    }

    public int getExcpcount() {
        return excpcount;
    }

    public void setExcpcount(int excpcount) {
        this.excpcount = excpcount;
    }

}
