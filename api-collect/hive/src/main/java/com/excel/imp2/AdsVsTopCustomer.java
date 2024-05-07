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


public class AdsVsTopCustomer implements ExcelSheetBI {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsVsTopCustomer(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
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
        ResultSet resultSetTopCustomer = ps1.executeQuery();
        ResultSet resultSetVsTotal = ps2.executeQuery();

        HashMap<Integer, Integer> vsTotalHashMap = new HashMap<>();

        while (resultSetVsTotal.next()) {
            vsTotalHashMap.put(resultSetVsTotal.getInt("rk1"), resultSetVsTotal.getInt("total_result2"));
        }

        int sameRk1 = -1;
        SXSSFSheet sheet7 = null;
        SXSSFRow row7FieldSheet7, row7Sheet7;

        while (resultSetTopCustomer.next()) {
            String customer = resultSetTopCustomer.getString("customer");
            String productSeries1 = resultSetTopCustomer.getString("product_series1");
            String productSeries2 = resultSetTopCustomer.getString("product_series2");
            int totalResult1 = resultSetTopCustomer.getInt("total_result1");
            int totalResult2 = resultSetTopCustomer.getInt("total_result2");
            int rank0 = resultSetTopCustomer.getInt("rank0");
            int team1 = resultSetTopCustomer.getInt("rk1");
            if (sameRk1 != team1) {
                sameRk1 = team1;
                sheet7 = xssfWorkbook.createSheet(productSeries1.replace(" ", "") + "-批发客户");
                sheet7.createRow(0).createCell(0).setCellValue(table);
                row7FieldSheet7 = sheet7.createRow(2);
                row7FieldSheet7.createCell(0).setCellValue("客户");
                row7FieldSheet7.createCell(1).setCellValue(productSeries1);
                row7FieldSheet7.createCell(2).setCellValue(productSeries2);
                try {
                    row7FieldSheet7.createCell(3).setCellValue(vsTotalHashMap.get(team1));
                } catch (Exception e) {
                }
            }
            row7Sheet7 = sheet7.createRow(rank0 + 2);
            row7Sheet7.createCell(0).setCellValue(customer);
            row7Sheet7.createCell(1).setCellValue(totalResult1);
            row7Sheet7.createCell(2).setCellValue(totalResult2);
        }

        ps1.close();
        ps2.close();
    }
}
