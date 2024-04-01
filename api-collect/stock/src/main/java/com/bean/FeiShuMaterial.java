package com.bean;

public class FeiShuMaterial {
    String materialName;
    String materialNumber;
    Integer wholeSale;
    Integer eCommerce;
    Integer branch;
    Integer danqu;
    Integer overseaCommerce;
    Integer tikTok;
    Integer refund;
    Integer free;
    Integer totalNumber;
    Integer etc1;
    Integer etc2;
    Double produceCost;
    Double costPrice;
    Double price;
    String projectID;
    String buyForm;
    String productLineCode;
    String productSeries;
    String prodcutType;

    public Integer getEtc1() {
        return etc1;
    }

    public void setEtc1(Integer etc1) {
        this.etc1 = etc1;
    }

    public Integer getEtc2() {
        return etc2;
    }

    public void setEtc2(Integer etc2) {
        this.etc2 = etc2;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getProductSeries() {
        return productSeries;
    }

    public void setProductSeries(String productSeries) {
        this.productSeries = productSeries;
    }

    public String getProdcutType() {
        return prodcutType;
    }

    public void setProdcutType(String prodcutType) {
        this.prodcutType = prodcutType;
    }

    public Integer getFree() {
        return free;
    }

    public void setFree(Integer free) {
        this.free = free;
    }

    public Integer getRefund() {
        return refund;
    }

    public void setRefund(Integer refund) {
        this.refund = refund;
    }

    public Double getProduceCost() {
        return produceCost;
    }

    public void setProduceCost(Double produceCost) {
        this.produceCost = produceCost;
    }

    public String getProductLineCode() {
        return productLineCode;
    }

    public void setProductLineCode(String productLineCode) {
        this.productLineCode = productLineCode;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getBuyForm() {
        return buyForm;
    }

    public void setBuyForm(String buyForm) {
        this.buyForm = buyForm;
    }



    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public Integer getWholeSale() {
        return wholeSale;
    }

    public void setWholeSale(Integer wholeSale) {
        this.wholeSale = wholeSale;
    }

    public Integer geteCommerce() {
        return eCommerce;
    }

    public void seteCommerce(Integer eCommerce) {
        this.eCommerce = eCommerce;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public Integer getDanqu() {
        return danqu;
    }

    public void setDanqu(Integer danqu) {
        this.danqu = danqu;
    }

    public Integer getOverseaCommerce() {
        return overseaCommerce;
    }

    public void setOverseaCommerce(Integer overseaCommerce) {
        this.overseaCommerce = overseaCommerce;
    }

    public Integer getTikTok() {
        return tikTok;
    }

    public void setTikTok(Integer tikTok) {
        this.tikTok = tikTok;
    }

    @Override
    public String toString() {
        return "FeiShuMaterial{" +
                "materialName='" + materialName + '\'' +
                ", materialNumber='" + materialNumber + '\'' +
                ", wholeSale=" + wholeSale +
                ", eCommerce=" + eCommerce +
                ", branch=" + branch +
                ", danqu=" + danqu +
                ", overseaCommerce=" + overseaCommerce +
                ", tikTok=" + tikTok +
                ", refund=" + refund +
                ", free=" + free +
                ", totalNumber=" + totalNumber +
                ", etc1=" + etc1 +
                ", etc2=" + etc2 +
                ", produceCost=" + produceCost +
                ", costPrice=" + costPrice +
                ", price=" + price +
                ", projectID='" + projectID + '\'' +
                ", buyForm='" + buyForm + '\'' +
                ", productLineCode='" + productLineCode + '\'' +
                ", productSeries='" + productSeries + '\'' +
                ", prodcutType='" + prodcutType + '\'' +
                '}';
    }
}
