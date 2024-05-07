package com.excel.imp2;

import com.excel.ExcelSheetBI;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;


public class AdsSelf implements ExcelSheetBI {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsSelf(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
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

        SXSSFSheet sheet4 = xssfWorkbook.createSheet(table);
        SXSSFRow sheetName = sheet4.createRow(0);
        sheetName.createCell(0).setCellValue(table);
        SXSSFRow rowField4 = sheet4.createRow(2);
        rowField4.createCell(0).setCellValue("产品线");
        rowField4.createCell(1).setCellValue("业务线");
        rowField4.createCell(2).setCellValue("产品系列");
        rowField4.createCell(3).setCellValue("上市日期");
        rowField4.createCell(4).setCellValue("发售信息");
        for (int i = 1; i <= 14; i++) {
            rowField4.createCell((i - 1) + 5).setCellValue("Day" + i);
        }
        rowField4.createCell(19).setCellValue("合计");


        ResultSet resultSet4 = ps1.executeQuery();
        int count4 = 3, sum, daySaleQuantity;
        SXSSFRow rowSheet4 = null;
        SXSSFCell cell3RowSheet4 = null;
        String tempProductLine = "盲盒", table4ProductLine;
        while (resultSet4.next()) {
            table4ProductLine = resultSet4.getString("product_line");
            if (!tempProductLine.equals(table4ProductLine)) {
                count4++;
            }
            tempProductLine = table4ProductLine;
            rowSheet4 = sheet4.createRow(count4);
            rowSheet4.createCell(0).setCellValue(table4ProductLine);
            rowSheet4.createCell(1).setCellValue(resultSet4.getString("business_line"));
            rowSheet4.createCell(2).setCellValue(resultSet4.getString("product_series"));
            cell3RowSheet4 = rowSheet4.createCell(3);
            cell3RowSheet4.setCellStyle(dateCellStyle);
            cell3RowSheet4.setCellValue(resultSet4.getDate("sale_time"));
            rowSheet4.createCell(4).setCellValue("现货发售");
            sum = 0;
            for (int i = 1; i <= 14; i++) {
                daySaleQuantity = resultSet4.getInt("day" + i);
                rowSheet4.createCell((i - 1) + 5).setCellValue(daySaleQuantity);
                sum += daySaleQuantity;
            }
            rowSheet4.createCell(19).setCellValue(sum);
            count4++;
        }

        ps1.close();
    }
}
