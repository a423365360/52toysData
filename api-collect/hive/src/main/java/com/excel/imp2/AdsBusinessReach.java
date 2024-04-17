package com.excel.imp2;

import com.excel.ExcelSheetBI;
import com.util.Util;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
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

public class AdsBusinessReach implements ExcelSheetBI {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsBusinessReach(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql1, String sql2, String dt) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));

        CellStyle percentStyle = xssfWorkbook.createCellStyle();
        percentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));

        // 新品对标产品
        PreparedStatement ps1 = hiveConnection.prepareStatement(sql1);
        PreparedStatement ps2 = hiveConnection.prepareStatement(sql2);

        SXSSFSheet sheet1 = xssfWorkbook.createSheet(table);
        SXSSFRow rowField1 = sheet1.createRow(0);
        rowField1.createCell(0).setCellValue("业务线");
        rowField1.createCell(1).setCellValue("本" + Util.mapFieldName(table) + "销售额(万元)");
        rowField1.createCell(2).setCellValue("预估本" + Util.mapFieldName(table) + "销售(万元)");
        rowField1.createCell(3).setCellValue("预估达成率");
        rowField1.createCell(4).setCellValue("时间进度(万元)");
        rowField1.createCell(5).setCellValue("差额(万元)");
        rowField1.createCell(6).setCellValue("去年同" + Util.mapFieldName(table) + "金额(万元)");
        ResultSet resultSet1 = ps1.executeQuery();
        ResultSet resultSet1T = ps2.executeQuery();
        int count1 = 1;
        double monthReach = 0, businessLinePreValue = 0, lastYear = 0;
        SXSSFRow rowSheet1;
        SXSSFCell cell3RowSheet1;
        while (resultSet1.next()) {
            rowSheet1 = sheet1.createRow(count1);
            rowSheet1.createCell(0).setCellValue(resultSet1.getString("business_line"));
            rowSheet1.createCell(1).setCellValue(new BigDecimal(resultSet1.getDouble("month_reach") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            monthReach += resultSet1.getDouble("month_reach");
            rowSheet1.createCell(2).setCellValue(new BigDecimal(resultSet1.getDouble("business_line_pre_value") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            businessLinePreValue += resultSet1.getDouble("business_line_pre_value");
            cell3RowSheet1 = rowSheet1.createCell(3);
            cell3RowSheet1.setCellStyle(percentStyle);
            cell3RowSheet1.setCellValue(resultSet1.getDouble("reach_percent"));
            rowSheet1.createCell(4).setCellValue(new BigDecimal(resultSet1.getDouble("time_reach") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            rowSheet1.createCell(5).setCellValue(new BigDecimal(resultSet1.getDouble("offset") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            rowSheet1.createCell(6).setCellValue(new BigDecimal(resultSet1.getDouble("last_same_month_reach") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            lastYear += resultSet1.getDouble("last_same_month_reach");
            count1++;
        }
        // 合计
        SXSSFRow row1End = sheet1.createRow(count1 + 1);
        row1End.createCell(0).setCellValue("合计");


        if (resultSet1T.next()) {
            row1End.createCell(1).setCellValue(new BigDecimal(resultSet1T.getDouble("month_reach") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row1End.createCell(2).setCellValue(new BigDecimal(resultSet1T.getDouble("business_line_pre_value") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            SXSSFCell cell3E = row1End.createCell(3);
            cell3E.setCellStyle(percentStyle);
            cell3E.setCellValue(resultSet1T.getDouble("reach_percent"));
            row1End.createCell(4).setCellValue(new BigDecimal(resultSet1T.getDouble("time_reach") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row1End.createCell(5).setCellValue(new BigDecimal(resultSet1T.getDouble("offset") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row1End.createCell(6).setCellValue(new BigDecimal(resultSet1T.getDouble("last_same_month_reach") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());

            // 其他
            SXSSFRow row1Etc = sheet1.createRow(count1);
            row1Etc.createCell(0).setCellValue("其他");
            row1Etc.createCell(1).setCellValue(new BigDecimal((resultSet1T.getDouble("month_reach") - monthReach) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row1Etc.createCell(2).setCellValue(new BigDecimal((resultSet1T.getDouble("business_line_pre_value") - businessLinePreValue) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            row1Etc.createCell(6).setCellValue(new BigDecimal((resultSet1T.getDouble("last_same_month_reach") - lastYear) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        ps1.close();
        ps2.close();
    }
}
