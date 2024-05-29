package com.bean;

import java.sql.Date;

public class YoucaihuaBusiness {
    String businessName;
    Date fdate;
    Double inCoin;
    Double inCome;
    Double consumeCoin;
    Double gift;
    int day;

    public YoucaihuaBusiness(String businessName, Date fdate, Double inCoin, Double inCome, Double consumeCoin, int day, Double gift) {
        this.businessName = businessName;
        this.fdate = fdate;
        this.inCoin = inCoin;
        this.inCome = inCome;
        this.consumeCoin = consumeCoin;
        this.day = day;
        this.gift = gift;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Date getFdate() {
        return fdate;
    }

    public void setFdate(Date fdate) {
        this.fdate = fdate;
    }

    public Double getInCoin() {
        return inCoin;
    }

    public void setInCoin(Double inCoin) {
        this.inCoin = inCoin;
    }

    public Double getInCome() {
        return inCome;
    }

    public void setInCome(Double inCome) {
        this.inCome = inCome;
    }

    public Double getConsumeCoin() {
        return consumeCoin;
    }

    public void setConsumeCoin(Double consumeCoin) {
        this.consumeCoin = consumeCoin;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Double getGift() {
        return gift;
    }

    public void setGift(Double gift) {
        this.gift = gift;
    }
}
