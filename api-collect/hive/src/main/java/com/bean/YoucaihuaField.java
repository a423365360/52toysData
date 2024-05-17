package com.bean;

public class YoucaihuaField {
    String business;
    String machine;
    String goodsName;

    public YoucaihuaField(String business, String machine, String goodsName) {
        this.business = business;
        this.machine = machine;
        this.goodsName = goodsName;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    @Override
    public String toString() {
        return "YoucaihuaField{" +
                "business='" + business + '\'' +
                ", machine='" + machine + '\'' +
                ", goodsName='" + goodsName + '\'' +
                '}';
    }
}
