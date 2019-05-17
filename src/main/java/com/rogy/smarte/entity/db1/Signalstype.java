package com.rogy.smarte.entity.db1;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * The persistent class for the signalstype database table.
 */
@Entity
//@NamedQueries({
//	@NamedQuery(name="Signalstype.findAll", query="SELECT s FROM Signalstype s ORDER BY s.typeCode ASC"),
//	@NamedQuery(name="Signalstype.findByCode", query="SELECT s FROM Signalstype s WHERE s.typeCode = :code"),
//	@NamedQuery(name="Signalstype.findBySignalsTypeID", query="SELECT s FROM Signalstype s WHERE s.signalsTypeID = :id")
//})
public class Signalstype implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DL = "1"; //电流
    public static final String DQYWGDL = "2"; //当前月无功电量
    public static final String DQYYGDL = "3"; //当前月有功电量
    public static final String DY = "4"; //电压
    public static final String GLYS = "5"; //功率因数
    public static final String PL = "6"; //频率
    public static final String WD = "7"; //温度
    public static final String WGDL = "8"; //无功电量
    public static final String WGGL = "9"; //无功功率
    public static final String YGDL = "10"; //有功电量
    public static final String YGGL = "11"; //有功功率

    @Id
    private Short signalsTypeID;

    private String type;

    private String typeCode;

    private String typeName;

    private String unit;

    public Signalstype() {
    }

    public Short getSignalsTypeID() {
        return this.signalsTypeID;
    }

    public void setSignalsTypeID(Short signalsTypeID) {
        this.signalsTypeID = signalsTypeID;
    }

    public String getTypeCode() {
        return this.typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{TypeID=" + signalsTypeID + ", TypeCode="
                + typeCode + ", TypeName=" + typeName + ", Unit=" + unit + "}";
    }

}
