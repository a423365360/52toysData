package com.constant;

import cn.hutool.core.date.DateTime;

import java.sql.Date;

public class StockConstant {
   public static Date HIDE_DATE = DateTime.of("2023-01-01", "yyyy-MM-dd").toSqlDate();
   public static Date MAX_DATE = DateTime.of("5000-01-01", "yyyy-MM-dd").toSqlDate();
}
