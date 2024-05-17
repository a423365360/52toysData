package com.bean;

public class Youcaihua {
    private Integer dayOfMonth;
    private Double saleMoney;
    private String goodsName;
    private Integer coin;
    private String business;
    private String machine;
    private Integer saleNumber;

    public Integer getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(Integer saleNumber) {
        this.saleNumber = saleNumber;
    }

    public Youcaihua(Integer dayOfMonth, Double saleMoney, String goodsName, Integer coin, String business, String machine, Integer saleNumber) {
        this.dayOfMonth = dayOfMonth;
        this.saleMoney = saleMoney;
        this.goodsName = goodsName;
        this.coin = coin;
        this.business = business;
        this.machine = machine;
        this.saleNumber = saleNumber;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public Double getSaleMoney() {
        return saleMoney;
    }

    public void setSaleMoney(Double saleMoney) {
        this.saleMoney = saleMoney;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
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

    @Override
    public String toString() {
        return "Youcaihua{" +
                "dayOfMonth=" + dayOfMonth +
                ", saleMoney=" + saleMoney +
                ", goodsName='" + goodsName + '\'' +
                ", coin=" + coin +
                ", business='" + business + '\'' +
                ", machine='" + machine + '\'' +
                ", saleNumber=" + saleNumber +
                '}';
    }
}
