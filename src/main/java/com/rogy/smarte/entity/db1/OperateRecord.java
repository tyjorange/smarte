package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The persistent class for the operate_record database table.
 */
@Entity
@Table(name = "operate_record")
//@NamedQuery(name = "OperateRecord.findAll", query = "SELECT o FROM OperateRecord o")
public class OperateRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    private String desc;

    private Timestamp time;

    // bi-directional many-to-one association to Fsu
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    public OperateRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
