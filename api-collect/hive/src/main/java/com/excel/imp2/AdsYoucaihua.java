package com.excel.imp2;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.bean.Youcaihua;
import com.bean.YoucaihuaField;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
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


public class AdsYoucaihua {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsYoucaihua(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    public void setSheet(String table, String sql1, String sql2, DateTime yesterday) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));
        String month1 = DateUtil.dateNew(yesterday).toDateStr().substring(0, 7) + "-01";

        PreparedStatement ps1 = hiveConnection.prepareStatement(sql1);
        PreparedStatement ps2 = hiveConnection.prepareStatement(sql2);

        SXSSFSheet sheetDayInMonth = xssfWorkbook.createSheet(table);
        ResultSet rs1 = ps1.executeQuery();
        ResultSet rs2 = ps2.executeQuery();

        ArrayList<Youcaihua> daySaleArrayList = new ArrayList<>();
        HashMap<Integer, YoucaihuaField> businessLineField = new HashMap<>();
//        HashMap<Integer, Double> dateTotalSale = new HashMap<>();

        while (rs1.next()) {
            int dayOfMonth = rs1.getInt("id");
            String business = rs1.getString("business");
            String machine = rs1.getString("machine");
            String goodsName = rs1.getString("goods_name");
            Date fdate = rs1.getDate("fdate");
            Double saleMoney = rs1.getDouble("sale_money");
            Integer saleNumber = rs1.getInt("sale_number");
            Integer coin = rs1.getInt("coin");
            Youcaihua youcaihua = new Youcaihua(dayOfMonth, saleMoney, goodsName, coin, business, machine, saleNumber);
            daySaleArrayList.add(youcaihua);
        }

        while (rs2.next()) {
            int id = rs2.getInt("id");
            String business = rs2.getString("business");
            String machine = rs2.getString("machine");
            String goodsName = rs2.getString("goods_name");
            YoucaihuaField youcaihuaField = new YoucaihuaField(business, machine, goodsName);
            businessLineField.put(id, youcaihuaField);
        }

        int businessNumber = businessLineField.size();

        SXSSFRow sheetName = sheetDayInMonth.createRow(0);
        sheetName.createCell(0).setCellValue(table);
        SXSSFRow weekField = sheetDayInMonth.createRow(1);
        SXSSFRow dateField = sheetDayInMonth.createRow(2);
//        SXSSFRow totalRow = sheetDayInMonth.createRow(4 + businessNumber);

        dateField.createCell(0).setCellValue("门店");
        dateField.createCell(1).setCellValue("机器名称");
        dateField.createCell(2).setCellValue("商品名称");
        int days = yesterday.dayOfMonth();
        for (int i = 0; i < days; i++) {
            DateTime date0 = DateTime.of(month1, "yyyy-MM-dd").offset(DateField.HOUR, 24 * i);
            sheetName.createCell(2 * i + 3).setCellValue("入币");
            sheetName.createCell(2 * i + 4).setCellValue("出礼品");
            dateField.createCell(2 * i + 3).setCellValue(date0.toDateStr());
            dateField.createCell(2 * i + 4).setCellValue(date0.toDateStr());
            weekField.createCell(2 * i + 3).setCellValue(date0.dayOfWeekEnum().toChinese());
            weekField.createCell(2 * i + 4).setCellValue(date0.dayOfWeekEnum().toChinese());
        }

        SXSSFRow row;
        for (int i = 1; i <= businessNumber; i++) {
            row = sheetDayInMonth.createRow(i + 2);
            YoucaihuaField youcaihuaField = businessLineField.get(i);
            row.createCell(0).setCellValue(youcaihuaField.getBusiness());
            row.createCell(1).setCellValue(youcaihuaField.getMachine());
            row.createCell(2).setCellValue(youcaihuaField.getGoodsName());
            for (int j = 1; j <= days; j++) {
                for (Youcaihua unit : daySaleArrayList) {
                    try {
                        if (unit.getBusiness().equals(youcaihuaField.getBusiness())
                                && unit.getMachine().equals(youcaihuaField.getMachine())
                                && unit.getGoodsName().equals(youcaihuaField.getGoodsName())
                                && unit.getDayOfMonth() == j) {
                            row.createCell(2 * j + 1).setCellValue(unit.getCoin());
                            row.createCell(2 * j + 2).setCellValue(unit.getSaleNumber());
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

//        totalRow.createCell(0).setCellValue("合计");
//        while (rsDayInMonthTotal.next()) {
//            Date fdate = rsDayInMonthTotal.getDate("fdate");
//            totalRow.createCell(DateTime.of(fdate).dayOfMonth())
//                    .setCellValue(new BigDecimal(rsDayInMonthTotal.getDouble("sale_money") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            dateTotalSale.put(DateTime.of(fdate).dayOfMonth(), rsDayInMonthTotal.getDouble("sale_money"));
//        }

        ps1.close();
        ps2.close();
    }
}
