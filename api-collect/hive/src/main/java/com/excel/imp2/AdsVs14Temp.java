package com.excel.imp2;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.excel.ExcelSheetBI;
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
import java.util.HashMap;
import java.util.HashSet;

public class AdsVs14Temp implements ExcelSheetBI {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsVs14Temp(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql1, String sql2, String dt) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));

        // 新品对标产品
        PreparedStatement ps1 = hiveConnection.prepareStatement(sql1);
        SXSSFSheet sheet6 = xssfWorkbook.createSheet(table);
        ResultSet resultSet5 = ps1.executeQuery();
        int count6 = 0, sameFlag1 = -1, sameFlag2 = -1;
        HashSet<Integer> rk1List = new HashSet<>();
        ArrayList<Integer> days;
        SXSSFRow row61, row62, rowSheet6;
        while (resultSet5.next()) {
            int rk1 = resultSet5.getInt("rk1");
            int rk2 = resultSet5.getInt("rk2");
            String productSeries = resultSet5.getString("product_series");
            String businessLine = resultSet5.getString("business_line");
            Date saleDate = resultSet5.getDate("sale_date");
            days = new ArrayList<>();

            int max = (int) DateUtil.betweenDay(saleDate, DateTime.of(dt, "yyyy-MM-dd"), true) + 1;

            if (DateUtil.compare(DateTime.of(dt, "yyyy-MM-dd"), saleDate) < 0) {
                max = max * (-1);
            }

            if (max < 1) {
                rk1List.add(rk1);
                if (sameFlag1 != rk1 || sameFlag2 != rk2) {
                    sameFlag1 = rk1;
                    sameFlag2 = rk2;
                    count6 += 2;
                    row61 = sheet6.createRow(count6);
                    row61.createCell(0).setCellValue(rk1);
                    row61.createCell(1).setCellValue(rk2 == 1 ? "新品" : "对标产品");
                    row61.createCell(2).setCellValue(productSeries);
                    continue;
                } else {
                    continue;
                }
            }

            int maxDays = Math.min(14, max);

            for (int i = 1; i <= maxDays; i++) {
                days.add(resultSet5.getInt("day" + i));
            }

            if (sameFlag1 != rk1 || sameFlag2 != rk2) {
                sameFlag1 = rk1;
                sameFlag2 = rk2;
                count6 += 2;
                row61 = sheet6.createRow(count6);
                row61.createCell(2).setCellValue(productSeries);

                // 中文日期
                for (int columnNumber = 3; columnNumber < maxDays + 3; columnNumber++) {
                    row61.createCell(columnNumber).setCellValue(
                            DateUtil.dateNew(saleDate).offset(DateField.HOUR, 24 * (columnNumber - 3)).dayOfWeekEnum().toChinese());
                }

                count6++;
                row62 = sheet6.createRow(count6);
                row62.createCell(0).setCellValue("产品分组");
                row62.createCell(1).setCellValue("对标分组");
                row62.createCell(2).setCellValue("业务平台");

                // Day表头
                for (int i = 1; i <= maxDays; i++) {
                    int j = i + 14;
                    row62.createCell((i - 1) + 3).setCellValue("Day" + j);
                }
                row62.createCell(maxDays + 3).setCellValue("合计");
                count6++;
            }

            rowSheet6 = sheet6.createRow(count6);
            rowSheet6.createCell(0).setCellValue(rk1);
            rowSheet6.createCell(1).setCellValue(rk2 == 1 ? "新品" : "对标产品");
            rowSheet6.createCell(2).setCellValue(businessLine);
            rowSheet6.createCell(3).setCellValue(days.get(0));
            for (int i = 1; i < maxDays; i++) {
                rowSheet6.createCell((i - 1) + 4).setCellValue(days.get(i));
            }
            int sumRow6 = count6 + 1;
            char charStart = 'D';
            char charEnd = (char) (charStart + maxDays - 1);
            rowSheet6.createCell(maxDays + 3).setCellFormula("SUM(" + charStart + (sumRow6) + ":" + charEnd + (sumRow6) + ")");
            count6++;
        }

        ps1.close();
    }
}
