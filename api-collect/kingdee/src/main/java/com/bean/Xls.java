package com.bean;

public class Xls {
    String name;  //TODO 名称
    String sign;  //TODO 名称
    String code;  //TODO 名称
    String type;  //TODO 名称
    String must;  //TODO 名称
    String length;  //TODO 名称
    String defaultValue;  //TODO 名称

    @Override
    public String toString() {
        return "Xls{" +
                "name='" + name + '\'' +
                ", sign='" + sign + '\'' +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", must='" + must + '\'' +
                ", length='" + length + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMust() {
        return must;
    }

    public void setMust(String must) {
        this.must = must;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
