package com.excel.imp2;

import com.excel.ExcelSheetBI;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;


public class AdsMainCustomer implements ExcelSheetBI {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsMainCustomer(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
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
        PreparedStatement ps2 = hiveConnection.prepareStatement(sql2);
        // 主要系列

        SXSSFSheet sheet3 = xssfWorkbook.createSheet(table);

        ResultSet resultSet3F = ps1.executeQuery();
        SXSSFRow rowField3F = sheet3.createRow(0);
        int count3F = 1;
        while (resultSet3F.next()) {
            rowField3F.createCell(count3F).setCellValue(resultSet3F.getString("product_series"));
            count3F++;
        }

        // 主要客户
        ResultSet resultSet3 = ps2.executeQuery();
        int count3 = 1;
        SXSSFRow rowSheet3;
        while (resultSet3.next()) {
            rowSheet3 = sheet3.createRow(count3);
            rowSheet3.createCell(0).setCellValue(resultSet3.getString("customer"));
            for (int i = 1; i <= 10; i++) {
                rowSheet3.createCell(i).setCellValue(resultSet3.getInt("top" + i));
            }
            count3++;
        }
        SXSSFRow row3End = sheet3.createRow(count3);
        row3End.createCell(0).setCellValue("合计");
        row3End.createCell(1).setCellFormula("SUM(B2:B" + count3 + ")");
        row3End.createCell(2).setCellFormula("SUM(C2:C" + count3 + ")");
        row3End.createCell(3).setCellFormula("SUM(D2:D" + count3 + ")");
        row3End.createCell(4).setCellFormula("SUM(E2:E" + count3 + ")");
        row3End.createCell(5).setCellFormula("SUM(F2:F" + count3 + ")");
        row3End.createCell(6).setCellFormula("SUM(G2:G" + count3 + ")");
        row3End.createCell(7).setCellFormula("SUM(H2:H" + count3 + ")");
        row3End.createCell(8).setCellFormula("SUM(I2:I" + count3 + ")");
        row3End.createCell(9).setCellFormula("SUM(J2:J" + count3 + ")");
        row3End.createCell(10).setCellFormula("SUM(K2:K" + count3 + ")");

        ps1.close();
        ps2.close();
    }
}
