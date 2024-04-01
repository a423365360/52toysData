package com.bean;

import java.util.Date;

public class CustomerDimBean {
    String customerName;
    String businessLine;
    Date startTime;
    Date endTime;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBusinessLine() {
        return businessLine;
    }

    public void setBusinessLine(String businessLine) {
        this.businessLine = businessLine;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "name: " + customerName + "\t" + "line: " + businessLine + "\t" + "start: " + startTime + "\t" + "end: " + endTime;
    }
}
