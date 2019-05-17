package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the fsu database table.
 */
@Entity
//@NamedQueries({
//        @NamedQuery(name = "Fsu.findAll", query = "SELECT f FROM Fsu f"),
//        @NamedQuery(name = "Fsu.findById", query = "SELECT f FROM Fsu f where f.fsuid = :id"),
//        @NamedQuery(name = "Fsu.findBySubegionID", query = "SELECT f FROM Fsu f where f.subregion.subRegionID = :subregionID"),
//        @NamedQuery(name = "Fsu.findByCode", query = "SELECT f FROM Fsu f WHERE f.FSUCode = :fsucode"),
//})
public class Fsu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String fsuid;

    private int cmdPort;

    private String FSUCode;

    private double FSULat;

    private Timestamp FSULLTime;

    private double FSULng;

    private String FSUName;

    private Timestamp heartbeatTime;

    private String ip;

    //bi-directional many-to-one association to Subregion
    @ManyToOne
    @JoinColumn(name = "SubRegionID")
    private Subregion subregion;

    public Fsu() {
    }

    public String getFsuid() {
        return this.fsuid;
    }

    public void setFsuid(String fsuid) {
        this.fsuid = fsuid;
    }

    public int getCmdPort() {
        return this.cmdPort;
    }

    public void setCmdPort(int cmdPort) {
        this.cmdPort = cmdPort;
    }

    public String getFSUCode() {
        return this.FSUCode;
    }

    public void setFSUCode(String FSUCode) {
        this.FSUCode = FSUCode;
    }

    public double getFSULat() {
        return this.FSULat;
    }

    public void setFSULat(double FSULat) {
        this.FSULat = FSULat;
    }

    public Timestamp getFSULLTime() {
        return this.FSULLTime;
    }

    public void setFSULLTime(Timestamp FSULLTime) {
        this.FSULLTime = FSULLTime;
    }

    public double getFSULng() {
        return this.FSULng;
    }

    public void setFSULng(double FSULng) {
        this.FSULng = FSULng;
    }

    public String getFSUName() {
        return this.FSUName;
    }

    public void setFSUName(String FSUName) {
        this.FSUName = FSUName;
    }

    public Timestamp getHeartbeatTime() {
        return this.heartbeatTime;
    }

    public void setHeartbeatTime(Timestamp heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Subregion getSubregion() {
        return this.subregion;
    }

    public void setSubregion(Subregion subregion) {
        this.subregion = subregion;
    }

}