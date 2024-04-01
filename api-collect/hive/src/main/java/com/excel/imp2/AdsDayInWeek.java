package com.excel.imp2;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AdsDayInWeek {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsDayInWeek(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    public void setSheet(String table, String sql1, String sql2, String sql3, DateTime yesterday) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));

        // 新品对标产品
        PreparedStatement ps1 = hiveConnection.prepareStatement(sql1);
        PreparedStatement ps2 = hiveConnection.prepareStatement(sql2);
        PreparedStatement ps3 = hiveConnection.prepareStatement(sql3);

        SXSSFSheet sheet2 = xssfWorkbook.createSheet(table);
        SXSSFRow rowField2 = sheet2.createRow(0);
        rowField2.createCell(0).setCellValue("业务线");
        rowField2.createCell(1).setCellValue("星期五");
        rowField2.createCell(2).setCellValue("星期六");
        rowField2.createCell(3).setCellValue("星期日");
        rowField2.createCell(4).setCellValue("星期一");
        rowField2.createCell(5).setCellValue("星期二");
        rowField2.createCell(6).setCellValue("星期三");
        rowField2.createCell(7).setCellValue("星期四");

        // 日期
        int k1 = yesterday.dayOfWeek();
        int k2 = k1 - 1;
        if (k2 < 1) {
            k2 = k2 + 7;
        }
        DateTime week = DateUtil.dateNew(yesterday);
        if (k2 > 4) {
            week = DateUtil.dateNew(yesterday).offset(DateField.HOUR, 24 * 7);
        }
        DateTime week1 = DateUtil.dateNew(week).offset(DateField.HOUR, -24 * (k2 - 1));
        DateTime week2 = DateUtil.dateNew(week1).offset(DateField.HOUR, 24);
        DateTime week3 = DateUtil.dateNew(week1).offset(DateField.HOUR, 24 * 2);
        DateTime week4 = DateUtil.dateNew(week1).offset(DateField.HOUR, 24 * 3);
        DateTime week5 = DateUtil.dateNew(week1).offset(DateField.HOUR, -24 * 3);
        DateTime week6 = DateUtil.dateNew(week1).offset(DateField.HOUR, -24 * 2);
        DateTime week7 = DateUtil.dateNew(week1).offset(DateField.HOUR, -24);

        SXSSFRow rowField2d = sheet2.createRow(1);
        SXSSFCell row21 = rowField2d.createCell(1);
        row21.setCellStyle(dateCellStyle);
        row21.setCellValue(week5);
        SXSSFCell row22 = rowField2d.createCell(2);
        row22.setCellStyle(dateCellStyle);
        row22.setCellValue(week6);
        SXSSFCell row23 = rowField2d.createCell(3);
        row23.setCellStyle(dateCellStyle);
        row23.setCellValue(week7);
        SXSSFCell row24 = rowField2d.createCell(4);
        row24.setCellStyle(dateCellStyle);
        row24.setCellValue(week1);
        SXSSFCell row25 = rowField2d.createCell(5);
        row25.setCellStyle(dateCellStyle);
        row25.setCellValue(week2);
        SXSSFCell row26 = rowField2d.createCell(6);
        row26.setCellStyle(dateCellStyle);
        row26.setCellValue(week3);
        SXSSFCell row27 = rowField2d.createCell(7);
        row27.setCellStyle(dateCellStyle);
        row27.setCellValue(week4);

        ResultSet resultSet2 = ps1.executeQuery();
        ResultSet resultSet2T = ps2.executeQuery();
//        ResultSet resultSet2E = ps3.executeQuery();
        int count2 = 2;
        double elseDay5 = 0, elseDay6 = 0, elseDay7 = 0, elseDay1 = 0, elseDay2 = 0, elseDay3 = 0, elseDay4 = 0;
        SXSSFRow rowSheet2;
        while (resultSet2.next()) {
            rowSheet2 = sheet2.createRow(count2);
            rowSheet2.createCell(0).setCellValue(resultSet2.getString("business_line"));
            rowSheet2.createCell(1).setCellValue(new BigDecimal(resultSet2.getDouble("day5") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            elseDay5 += resultSet2.getDouble("day5");
            rowSheet2.createCell(2).setCellValue(new BigDecimal(resultSet2.getDouble("day6") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            elseDay6 += resultSet2.getDouble("day6");
            rowSheet2.createCell(3).setCellValue(new BigDecimal(resultSet2.getDouble("day7") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            elseDay7 += resultSet2.getDouble("day7");
            rowSheet2.createCell(4).setCellValue(new BigDecimal(resultSet2.getDouble("day1") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            elseDay1 += resultSet2.getDouble("day1");
            rowSheet2.createCell(5).setCellValue(new BigDecimal(resultSet2.getDouble("day2") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            elseDay2 += resultSet2.getDouble("day2");
            rowSheet2.createCell(6).setCellValue(new BigDecimal(resultSet2.getDouble("day3") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            elseDay3 += resultSet2.getDouble("day3");
            rowSheet2.createCell(7).setCellValue(new BigDecimal(resultSet2.getDouble("day4") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            elseDay4 += resultSet2.getDouble("day4");
            count2++;
        }

        // 合计
        SXSSFRow row2End = sheet2.createRow(count2 + 1);
        row2End.createCell(0).setCellValue("合计");
        // 其他
        SXSSFRow row2Else = sheet2.createRow(count2);
        row2Else.createCell(0).setCellValue("其他");
        if (resultSet2T.next()) {
            // 合计
            double day5 = resultSet2T.getDouble("day5");
            double day6 = resultSet2T.getDouble("day6");
            double day7 = resultSet2T.getDouble("day7");
            double day1 = resultSet2T.getDouble("day1");
            double day2 = resultSet2T.getDouble("day2");
            double day3 = resultSet2T.getDouble("day3");
            double day4 = resultSet2T.getDouble("day4");
            row2End.createCell(1).setCellValue(new BigDecimal(day5 / 10000d).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2End.createCell(2).setCellValue(new BigDecimal(day6 / 10000d).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2End.createCell(3).setCellValue(new BigDecimal(day7 / 10000d).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2End.createCell(4).setCellValue(new BigDecimal(day1 / 10000d).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2End.createCell(5).setCellValue(new BigDecimal(day2 / 10000d).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2End.createCell(6).setCellValue(new BigDecimal(day3/ 10000d).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2End.createCell(7).setCellValue(new BigDecimal(day4 / 10000d).setScale(2, RoundingMode.HALF_UP).doubleValue());

            //其他
            row2Else.createCell(1).setCellValue(new BigDecimal((day5 - elseDay5) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2Else.createCell(2).setCellValue(new BigDecimal((day6- elseDay6) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2Else.createCell(3).setCellValue(new BigDecimal((day7 - elseDay7) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2Else.createCell(4).setCellValue(new BigDecimal((day1 - elseDay1) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2Else.createCell(5).setCellValue(new BigDecimal((day2 - elseDay2) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2Else.createCell(6).setCellValue(new BigDecimal((day3 - elseDay3) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row2Else.createCell(7).setCellValue(new BigDecimal((day4- elseDay4) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }

        // 蜡笔小新快闪店
//        SXSSFRow row2Kuai = sheet2.createRow(count2 + 2);
//        row2Kuai.createCell(0).setCellValue("蜡笔小新快闪店");
//        if (resultSet2E.next()) {
//            row2Kuai.createCell(1).setCellValue(new BigDecimal((resultSet2E.getDouble("day5")) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            row2Kuai.createCell(2).setCellValue(new BigDecimal((resultSet2E.getDouble("day6")) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            row2Kuai.createCell(3).setCellValue(new BigDecimal((resultSet2E.getDouble("day7")) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            row2Kuai.createCell(4).setCellValue(new BigDecimal((resultSet2E.getDouble("day1")) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            row2Kuai.createCell(5).setCellValue(new BigDecimal((resultSet2E.getDouble("day2")) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            row2Kuai.createCell(6).setCellValue(new BigDecimal((resultSet2E.getDouble("day3")) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            row2Kuai.createCell(7).setCellValue(new BigDecimal((resultSet2E.getDouble("day4")) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
//        }
        ps1.close();
        ps2.close();
//        ps3.close();
    }
}
