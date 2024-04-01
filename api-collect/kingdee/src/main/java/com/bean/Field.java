package com.bean;

public class Field {
    String name;
    String code;
    String dataType;
    String length;
    String precision;
    String p;
    String f;
    String m;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", dataType='" + dataType + '\'' +
                ", length='" + length + '\'' +
                ", precision='" + precision + '\'' +
                ", p='" + p + '\'' +
                ", f='" + f + '\'' +
                ", m='" + m + '\'' +
                '}';
    }
}
