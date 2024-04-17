package com.util;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;

public class Util {
    public static Connection getConnection(String database) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
//        Connection connection = DriverManager.getConnection("jdbc:hive2://8.140.57.104:10000/" + database, "52toys", "52toys.qwe123.mysql");
        Connection connection = DriverManager.getConnection("jdbc:hive2://172.20.116.176:10000/" + database, "52toys", "52toys.qwe123.mysql");
        return connection;
    }

    public static double mapNumber(double input) {
        return new BigDecimal(input).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public static String mapStockAge(Date input) {
        if (input == null) {
            return "";
        }
        Date today = DateTime.now().toSqlDate();
        long offset = DateUtil.between(input, today, DateUnit.DAY, false);
        if (offset <= 90) {
            return "1-3个月";
        } else if (offset > 90 && offset <= 180) {
            return "4-6个月";
        } else if (offset > 180 && offset <= 365) {
            return "7-12个月";
        } else if (offset > 365 && offset <= 730) {
            return "1-2年";
        } else if (offset > 730 && offset <= 1095) {
            return "2-3年";
        } else if (offset > 1095) {
            return "3年以上";
        }
        return "";
    }

    public static String trend(int qtyWeek1, int qtyWeek2) {
        String trend = "平稳";
        if (qtyWeek1 > qtyWeek2) {
            trend = "上涨";
        } else if (qtyWeek1 < qtyWeek2) {
            trend = "下跌";
        }
        return trend;
    }

    public static String stockStatus(int qty, int saleQty, int days, Date saleDate, Date now) {
        String stockSatus = "";
        if (saleQty != 0) {
            double stockIndex = ((double) qty) / ((double) saleQty) * ((double) days);

            if (stockIndex < 70) {
                stockSatus = "缺货";
            } else if (stockIndex <= 120) {
                stockSatus = "正常";
            } else {
                stockSatus = "溢出";
            }
        }
        if (saleDate != null && saleDate.after(now)) {
            stockSatus = "未上市";
        }
        return stockSatus;
    }

    public static String mapFieldName(String input) {
        if (input == null){
            return "";
        }
        if (input.contains("清货")){
            return "月";
        }
        if (input.contains("季度")){
            return "季";
        }

        return "月";
    }

}
