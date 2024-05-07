package com.excel.imp2;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.bean.DaySale;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;


public class AdsDayInMonth {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsDayInMonth(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    public void setSheet(String table, String sql1, String sql2, String sql3, DateTime yesterday) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));
        String month1 = DateUtil.dateNew(yesterday).toDateStr().substring(0, 7) + "-01";

        PreparedStatement ps1 = hiveConnection.prepareStatement(sql1);
        PreparedStatement ps2 = hiveConnection.prepareStatement(sql2);
        PreparedStatement ps3 = hiveConnection.prepareStatement(sql3);

        SXSSFSheet sheetDayInMonth = xssfWorkbook.createSheet(table);
        ResultSet rsDayInMonth = ps1.executeQuery();
        ResultSet rsDayInMonthTotal = ps2.executeQuery();
        ResultSet rsDayInMonthExtra = ps3.executeQuery();

        ArrayList<DaySale> daySaleArrayList = new ArrayList<>();
        ArrayList<DaySale> daySaleExtraArrayList = new ArrayList<>();
        HashMap<Integer, String> businessLineField = new HashMap<>();
        HashMap<Integer, Double> dateTotalSale = new HashMap<>();

        while (rsDayInMonth.next()) {
            int id = rsDayInMonth.getInt("id");
            String businessLine = rsDayInMonth.getString("business_line");
            Date fdate = rsDayInMonth.getDate("fdate");
            Double saleMoney = rsDayInMonth.getDouble("sale_money");
            int dayNumber = DateTime.of(fdate).dayOfMonth();
            daySaleArrayList.add(new DaySale(id, dayNumber, saleMoney));
            businessLineField.put(id, businessLine);
        }

        //TODO TEST
        while (rsDayInMonthExtra.next()) {
            Date fdate = rsDayInMonthExtra.getDate("fdate");
            Double saleMoney = rsDayInMonthExtra.getDouble("sale_money");
            int dayNumber = DateTime.of(fdate).dayOfMonth();
            daySaleExtraArrayList.add(new DaySale(1, dayNumber, saleMoney));
        }

        int businessNumber = businessLineField.size();
        SXSSFRow sheetName = sheetDayInMonth.createRow(0);
        sheetName.createCell(0).setCellValue(table);
        SXSSFRow weekField = sheetDayInMonth.createRow(1);
        SXSSFRow dateField = sheetDayInMonth.createRow(2);
        SXSSFRow elseRow = sheetDayInMonth.createRow(3 + businessNumber);
        SXSSFRow totalRow = sheetDayInMonth.createRow(4 + businessNumber);
        SXSSFRow extraRow = sheetDayInMonth.createRow(5 + businessNumber);

        weekField.createCell(0).setCellValue("业务线");
        int days = yesterday.dayOfMonth();
        for (int i = 0; i < days; i++) {
            DateTime date0 = DateTime.of(month1, "yyyy-MM-dd").offset(DateField.HOUR, 24 * i);
            dateField.createCell(i + 1).setCellValue(date0.toDateStr());
            weekField.createCell(i + 1).setCellValue(date0.dayOfWeekEnum().toChinese());
        }

        //TODO TEST
        SXSSFRow rowSheetDayInMonth;
        for (int i = 1; i <= businessNumber; i++) {
            rowSheetDayInMonth = sheetDayInMonth.createRow(i + 2);
            rowSheetDayInMonth.createCell(0).setCellValue(businessLineField.get(i));
            for (int j = 1; j <= days; j++) {
                for (DaySale unit : daySaleArrayList) {
                    if (unit.getId() == i && unit.getDayOfMonth() == j) {
                        rowSheetDayInMonth.createCell(j).setCellValue(new BigDecimal(unit.getSaleMoney() / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    }
                }
            }
        }

        totalRow.createCell(0).setCellValue("合计");
        while (rsDayInMonthTotal.next()) {
            Date fdate = rsDayInMonthTotal.getDate("fdate");
            totalRow.createCell(DateTime.of(fdate).dayOfMonth())
                    .setCellValue(new BigDecimal(rsDayInMonthTotal.getDouble("sale_money") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            dateTotalSale.put(DateTime.of(fdate).dayOfMonth(), rsDayInMonthTotal.getDouble("sale_money"));
        }

        elseRow.createCell(0).setCellValue("其他");
        for (int j = 1; j <= days; j++) {
            try {
                Double totalSale = dateTotalSale.get(j);
                Double sum = 0d;
                for (DaySale unit : daySaleArrayList) {
                    if (unit.getDayOfMonth() == j) {
                        sum += unit.getSaleMoney();
                    }
                }
                elseRow.createCell(j).setCellValue(new BigDecimal((totalSale - sum) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            } catch (Exception e) {
            }
        }

        // 油菜花
        extraRow.createCell(0).setCellValue("直营门店-娃娃机");
        for (int j = 1; j < 31; j++) {
            for (DaySale unit : daySaleExtraArrayList) {
                if (unit.getDayOfMonth() == j) {
                    extraRow.createCell(j).setCellValue(new BigDecimal(unit.getSaleMoney() / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }
            }
        }

        ps1.close();
        ps2.close();
        ps3.close();
    }
}
