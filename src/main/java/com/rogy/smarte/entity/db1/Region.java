package com.rogy.smarte.entity.db1;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;


/**
 * The persistent class for the region database table.
 */
@Entity
//@NamedQueries({
//        @NamedQuery(name = "Region.findAll", query = "SELECT r FROM Region r"),
//        @NamedQuery(name = "Region.findById", query = "SELECT r FROM Region r where r.regionID = :regionid"),
//        @NamedQuery(name = "Region.findByCode", query = "SELECT r FROM Region r WHERE r.regionCode = :regioncode"),
//})
public class Region implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String regionID;

    private String regionCode;

    private String regionName;

    public Region() {
    }

    public String getRegionID() {
        return this.regionID;
    }

    public void setRegionID(String regionID) {
        this.regionID = regionID;
    }

    public String getRegionCode() {
        return this.regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return this.regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

}