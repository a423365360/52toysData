package com.excel.imp2;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.bean.YoucaihuaBusiness;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class AdsYoucaihuaBusiness {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsYoucaihuaBusiness(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    public void setSheet(String table, String sql1, String sql2, DateTime yesterday) throws Exception {
        // 设置日期格式
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));

        // 获取当月第一天
        String month1 = DateUtil.dateNew(yesterday).toDateStr().substring(0, 7) + "-01";
        int dayMax = yesterday.dayOfMonth();
        double[] sum1 = new double[dayMax];
        double[] sum2 = new double[dayMax];
        double[] sum3 = new double[dayMax];
        double[] sum4 = new double[dayMax];

        // 预编译sql
        PreparedStatement ps1 = hiveConnection.prepareStatement(sql1);

        // 添加sheet
        SXSSFSheet sheet = xssfWorkbook.createSheet(table);

        // 获取结果集
        ResultSet rs1 = ps1.executeQuery();

        // 设置数据容器
        HashSet<String> businessSet = new HashSet<>();
        ArrayList<YoucaihuaBusiness> arrayList = new ArrayList<>();

        // 装载数据
        while (rs1.next()) {
            String businessName = rs1.getString("business_name");
            Date fdate = rs1.getDate("fdate");
            Double inCoin = rs1.getDouble("in_coin");
            Double inCome = rs1.getDouble("in_come");
            Double consumeCoin = rs1.getDouble("consume_coin");
            Double gift = rs1.getDouble("gift");
            businessSet.add(businessName);
            int day = DateTime.of(fdate).dayOfMonth();
            arrayList.add(new YoucaihuaBusiness(businessName, fdate, inCoin, inCome, consumeCoin, day, gift));
        }

        // 生成表头
        SXSSFRow sheetName = sheet.createRow(0);
        sheetName.createCell(0).setCellValue(table);
        SXSSFRow weekField = sheet.createRow(1);
        SXSSFRow dateField = sheet.createRow(2);
        dateField.createCell(0).setCellValue("娃娃机");
        dateField.createCell(1).setCellValue("项目");
        int days = yesterday.dayOfMonth();
        for (int i = 0; i < days; i++) {
            DateTime date0 = DateTime.of(month1, "yyyy-MM-dd").offset(DateField.HOUR, 24 * i);
            dateField.createCell(i + 2).setCellValue(date0.toDateStr());
            weekField.createCell(i + 2).setCellValue(date0.dayOfWeekEnum().toChinese());
        }

        int row = 3;
        Iterator<String> it = businessSet.iterator();
        while (it.hasNext()) {
            String branch = it.next();
            SXSSFRow row1 = sheet.createRow(row++);
            SXSSFRow row2 = sheet.createRow(row++);
            SXSSFRow row3 = sheet.createRow(row++);
            SXSSFRow row4 = sheet.createRow(row++);
            row1.createCell(0).setCellValue(branch);
            row1.createCell(1).setCellValue("收入金额");
            row2.createCell(1).setCellValue("充币数");
            row3.createCell(1).setCellValue("消耗币数");
            row4.createCell(1).setCellValue("出礼品数");
            for (int i = 0; i < dayMax; i++) {
                for (YoucaihuaBusiness unit : arrayList) {
                    try {
                        if (branch.equals(unit.getBusinessName()) && ((i + 1) == unit.getDay())) {
                            double inCome = unit.getInCome();
                            double inCoin = unit.getInCoin();
                            double consumeCoin = unit.getConsumeCoin();
                            double gift = unit.getGift();
                            row1.createCell(i + 2).setCellValue(inCome);
                            row2.createCell(i + 2).setCellValue(inCoin);
                            row3.createCell(i + 2).setCellValue(consumeCoin);
                            row4.createCell(i + 2).setCellValue(gift);
                            sum1[i] += inCome;
                            sum2[i] += inCoin;
                            sum3[i] += consumeCoin;
                            sum4[i] += gift;
                        }
                    } catch (Exception e) {
                        System.out.println("Error row");
                    }
                }
            }
        }

        SXSSFRow totalRow1 = sheet.createRow(row++);
        SXSSFRow totalRow2 = sheet.createRow(row++);
        SXSSFRow totalRow3 = sheet.createRow(row++);
        SXSSFRow totalRow4 = sheet.createRow(row);
        totalRow1.createCell(0).setCellValue("合计");
        totalRow1.createCell(1).setCellValue("收入金额");
        totalRow2.createCell(1).setCellValue("充币数");
        totalRow3.createCell(1).setCellValue("消耗币数");
        totalRow4.createCell(1).setCellValue("出礼品数");
        for (int i = 0; i < dayMax; i++) {
            totalRow1.createCell(i + 2).setCellValue(sum1[i]);
            totalRow2.createCell(i + 2).setCellValue(sum2[i]);
            totalRow3.createCell(i + 2).setCellValue(sum3[i]);
            totalRow4.createCell(i + 2).setCellValue(sum4[i]);
        }

        ps1.close();

    }
}
