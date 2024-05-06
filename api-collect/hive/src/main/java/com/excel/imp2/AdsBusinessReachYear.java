package com.excel.imp2;

import cn.hutool.core.date.DateTime;
import com.excel.ExcelSheetWeekBI;
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

public class AdsBusinessReachYear implements ExcelSheetWeekBI {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsBusinessReachYear(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql, String dt) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));

        CellStyle percentStyle = xssfWorkbook.createCellStyle();
        percentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));

        // 新品对标产品
        PreparedStatement ps1 = hiveConnection.prepareStatement(sql);

        SXSSFSheet sheet1 = xssfWorkbook.createSheet(table);
        SXSSFRow rowField0 = sheet1.createRow(0);
        rowField0.createCell(0).setCellValue(table);
        SXSSFRow rowField1 = sheet1.createRow(1);
        rowField1.createCell(0).setCellValue("业务线");
        rowField1.createCell(1).setCellValue("销售额(万元)");
        rowField1.createCell(2).setCellValue("年指标(万元)");
        rowField1.createCell(3).setCellValue("达成率");
        rowField1.createCell(4).setCellValue("权重");
        rowField1.createCell(5).setCellValue("截止周报预算金额(万元)");
        rowField1.createCell(6).setCellValue("累计达成与预算差额(万元)");
        ResultSet resultSet1 = ps1.executeQuery();
        int count1 = 2;
        double monthReach = 0, businessLinePreValue = 0;
        SXSSFRow rowSheet1;
        SXSSFCell cell3RowSheet1, cell3RowSheet2;
        while (resultSet1.next()) {
            rowSheet1 = sheet1.createRow(count1);
            rowSheet1.createCell(0).setCellValue(resultSet1.getString("business_line"));
            rowSheet1.createCell(1).setCellValue(new BigDecimal(resultSet1.getDouble("year_reach") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            monthReach += resultSet1.getDouble("year_reach");
            rowSheet1.createCell(2).setCellValue(new BigDecimal(resultSet1.getDouble("business_line_pre_value") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            businessLinePreValue += resultSet1.getDouble("business_line_pre_value");
            cell3RowSheet1 = rowSheet1.createCell(3);
            cell3RowSheet1.setCellStyle(percentStyle);
            cell3RowSheet1.setCellValue(resultSet1.getDouble("reach_percent"));
            cell3RowSheet2 = rowSheet1.createCell(4);
            cell3RowSheet2.setCellStyle(percentStyle);
            cell3RowSheet2.setCellValue(resultSet1.getDouble("weight"));
            rowSheet1.createCell(5).setCellValue(new BigDecimal(resultSet1.getDouble("time_reach") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
            rowSheet1.createCell(6).setCellValue(new BigDecimal(resultSet1.getDouble("offset") / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());

            count1++;
        }

        double totalSale = resultSet1.getDouble("total_sale");
        int days = DateTime.of(dt, "yyyy-MM-dd").dayOfYear();
        int yearDays = DateTime.of(dt.substring(0, 4) + "-12-31", "yyyy-MM-dd").dayOfYear();
        double totalTimeReach = businessLinePreValue * days / yearDays;

        // 合计
        int totalCount = count1 + 2;
        SXSSFRow row1End = sheet1.createRow(count1 + 1);
        row1End.createCell(0).setCellValue("合计");
        row1End.createCell(1).setCellValue(new BigDecimal(totalSale / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
        row1End.createCell(2).setCellValue(new BigDecimal(businessLinePreValue / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
        SXSSFCell cell3E = row1End.createCell(3);
        cell3E.setCellStyle(percentStyle);
        cell3E.setCellValue(totalSale / businessLinePreValue);
        SXSSFCell cellDefault = row1End.createCell(4);
        cellDefault.setCellStyle(percentStyle);
        cellDefault.setCellValue(1);

        try {
            row1End.createCell(5).setCellFormula("SUM(F2:F" + count1 + ")");
            row1End.createCell(6).setCellFormula("B" + totalCount + "-F" + totalCount);
        } catch (Exception e) {
        }

        // 其他
        SXSSFRow row1Etc = sheet1.createRow(count1);
        row1Etc.createCell(0).setCellValue("其他");
        row1Etc.createCell(1).setCellValue(new BigDecimal((totalSale - monthReach) / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue());
        SXSSFCell cellEtc = row1Etc.createCell(4);
        cellEtc.setCellStyle(percentStyle);
        cellEtc.setCellValue((totalSale - monthReach) / businessLinePreValue);

        ps1.close();
    }
}
