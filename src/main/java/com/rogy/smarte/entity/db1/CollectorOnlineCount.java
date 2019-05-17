package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "collector_onlinecount")
//@NamedQueries({
//        @NamedQuery(name = "CollectorOnlineCount.findAll", query = "SELECT c FROM CollectorOnlineCount c ORDER BY c.ontime")
//})
public class CollectorOnlineCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;
    private Timestamp ontime;
    private Integer oncount;
    private Integer ontopcount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getOntime() {
        return ontime;
    }

    public void setOntime(Timestamp ontime) {
        this.ontime = ontime;
    }

    public Integer getOncount() {
        return oncount;
    }

    public void setOncount(Integer oncount) {
        this.oncount = oncount;
    }

    public Integer getOntopcount() {
        return ontopcount;
    }

    public void setOntopcount(Integer ontopcount) {
        this.ontopcount = ontopcount;
    }

}
