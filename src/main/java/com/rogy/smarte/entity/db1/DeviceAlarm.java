package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The persistent class for the device_alarm database table.
 */
@Entity
@Table(name = "device_alarm")
//@NamedQueries({
//        @NamedQuery(name = "DeviceAlarm.findAll", query = "SELECT d FROM DeviceAlarm d order by d.time DESC"),
//        @NamedQuery(name = "DeviceAlarm.findByTime", query = "SELECT d FROM DeviceAlarm d WHERE d.time > :time order by d.time DESC"),
//        @NamedQuery(name = "DeviceAlarm.findByDevice", query = "SELECT d FROM DeviceAlarm d WHERE d.deviceID = :deviceID AND d.deviceType = :deviceType")
//})
public class DeviceAlarm implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    private int code;

    private Integer deviceID;

    private int deviceType;

    private Timestamp time;

    public DeviceAlarm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Integer getDeviceID() {
        return this.deviceID;
    }

    public void setDeviceID(Integer deviceID) {
        this.deviceID = deviceID;
    }

    public int getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

}
