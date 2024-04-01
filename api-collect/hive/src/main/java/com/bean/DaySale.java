package com.bean;

public class DaySale {
    private Integer id;
    private Integer dayOfMonth;
    private Double saleMoney;

    public DaySale(Integer id, Integer dayOfMonth, Double saleMoney) {
        this.id = id;
        this.dayOfMonth = dayOfMonth;
        this.saleMoney = saleMoney;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
