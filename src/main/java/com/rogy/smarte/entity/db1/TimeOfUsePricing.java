package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the time_of_use_pricing database table.
 */
@Entity
@Table(name = "time_of_use_pricing")
//@NamedQueries({
//	@NamedQuery(name="TimeOfUsePricing.findAll", query="SELECT t FROM TimeOfUsePricing t"),
//	@NamedQuery(name="TimeOfUsePricing.findByUserIdAndTimePoint", query="SELECT t FROM TimeOfUsePricing t WHERE t.user.id = :userID AND t.timePoint = :timePoint"),
//	@NamedQuery(name="TimeOfUsePricing.findByUserId", query="SELECT t FROM TimeOfUsePricing t WHERE t.user.id = :userID ORDER BY t.timePoint ASC")
//})
public class TimeOfUsePricing implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private double price;

    private int timePoint;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "userID")
    private User user;

    public TimeOfUsePricing() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTimePoint() {
        return this.timePoint;
    }

    public void setTimePoint(int timePoint) {
        this.timePoint = timePoint;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
