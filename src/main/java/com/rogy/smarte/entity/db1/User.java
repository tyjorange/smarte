package com.rogy.smarte.entity.db1;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name="user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String username;
    private String password;
    private BigInteger timeMills;
    private String nickName;
    private String headImg;
    private String phone;
    private String outerId;
    private String outerType;
    private String userUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigInteger getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(BigInteger timeMills) {
        this.timeMills = timeMills;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public String getOuterType() {
        return outerType;
    }

    public void setOuterType(String outerType) {
        this.outerType = outerType;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }
}
