package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the collector_share database table.
 */
@Entity
@Table(name = "collector_share")
//@NamedQueries({
//        @NamedQuery(name = "CollectorShare.findAll", query = "SELECT c FROM CollectorShare c"),
//        @NamedQuery(name = "CollectorShare.findByUserID", query = "SELECT c FROM CollectorShare c WHERE c.user.id = :userID"),
//        @NamedQuery(name = "CollectorShare.findByCollectorAndUser", query = "SELECT c FROM CollectorShare c WHERE c.collector.collectorID = :collectorID AND c.user.id = :userID"),
//        @NamedQuery(name = "CollectorShare.findByCollectorID", query = "SELECT c FROM CollectorShare c WHERE c.collector.collectorID = :collectorID")
//})
public class CollectorShare implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @ManyToOne
    @JoinColumn(name = "collectorID")
    private Collector collector;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    private int enable = 0;

    public CollectorShare() {
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

}
