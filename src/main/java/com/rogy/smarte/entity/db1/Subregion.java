package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the subregion database table.
 * 
 */
@Entity
//@NamedQueries({
//	@NamedQuery(name="Subregion.findAll", query="SELECT s FROM Subregion s"),
//	@NamedQuery(name="Subregion.findById", query="SELECT s FROM Subregion s where s.subRegionID = :subregionid"),
//	@NamedQuery(name="Subregion.findByCode", query="SELECT s FROM Subregion s WHERE s.subRegionCode = :subregioncode"),
//	@NamedQuery(name="Subregion.findByRegionID", query="SELECT s FROM Subregion s WHERE s.region.regionID = :regionID"),
//})
public class Subregion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String subRegionID;

	private String subRegionCode;

	private String subRegionName;

	//bi-directional many-to-one association to Region
	@ManyToOne
	@JoinColumn(name="RegionID")
	private Region region;

	public Subregion() {
	}

	public String getSubRegionID() {
		return this.subRegionID;
	}

	public void setSubRegionID(String subRegionID) {
		this.subRegionID = subRegionID;
	}

	public String getSubRegionCode() {
		return this.subRegionCode;
	}

	public void setSubRegionCode(String subRegionCode) {
		this.subRegionCode = subRegionCode;
	}

	public String getSubRegionName() {
		return this.subRegionName;
	}

	public void setSubRegionName(String subRegionName) {
		this.subRegionName = subRegionName;
	}

	public Region getRegion() {
		return this.region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

}