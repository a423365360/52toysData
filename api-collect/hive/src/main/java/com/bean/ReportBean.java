package com.bean;

public class ReportBean {
    private String filePath;
    private int reportType;
    private String dt;

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public ReportBean(String filePath, int reportType, String dt) {
        this.filePath = filePath;
        this.reportType = reportType;
        this.dt = dt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public ReportBean() {
    }

    public ReportBean(String filePath, int reportType) {
        this.filePath = filePath;
        this.reportType = reportType;
    }

}
