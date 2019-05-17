package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the electrical_equipment database table.
 */
@Entity
@Table(name = "electrical_equipment")
//@NamedQueries({
//        @NamedQuery(name = "ElectricalEquipment.findAll", query = "SELECT e FROM ElectricalEquipment e"),
//        @NamedQuery(name = "ElectricalEquipment.findBySwitchID", query = "SELECT e FROM ElectricalEquipment e WHERE e.switchs.switchID = :switchID"),
//        @NamedQuery(name = "ElectricalEquipment.findByID", query = "SELECT e FROM ElectricalEquipment e WHERE e.id = :id")
//})
public class ElectricalEquipment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private double gonglv;

    private String name;

    private Timestamp addTime;

    @ManyToOne
    @JoinColumn(name = "switchID")
    private Switch switchs;

    public ElectricalEquipment() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getGonglv() {
        return this.gonglv;
    }

    public void setGonglv(double gonglv) {
        this.gonglv = gonglv;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Switch getSwitchs() {
        return switchs;
    }

    public void setSwitchs(Switch switchs) {
        this.switchs = switchs;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

}