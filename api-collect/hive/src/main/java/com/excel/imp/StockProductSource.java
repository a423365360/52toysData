package com.excel.imp;

import cn.hutool.core.date.DateTime;
import com.excel.ExcelSheet;
import com.util.Util;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StockProductSource implements ExcelSheet {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public StockProductSource(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql) throws Exception {
        Date now = DateTime.now().toSqlDate();
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        CellStyle numberCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));
        numberCellStyle.setDataFormat(dataFormat.getFormat("#,##0"));

        // 使用 DataFormat 对象创建一个数字格式
        CellStyle periodStyle = xssfWorkbook.createCellStyle();
        periodStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));

        // 产品来源
        SXSSFSheet sheet = xssfWorkbook.createSheet(table);
        PreparedStatement ps = hiveConnection.prepareStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        sheet.createRow(0).createCell(0).setCellValue(table);

        SXSSFRow rowField = sheet.createRow(1);
        rowField.createCell(0).setCellValue("产品来源");
        rowField.createCell(1).setCellValue("全部库存");
        rowField.createCell(2).setCellValue("可用库存");
        rowField.createCell(3).setCellValue("全部库存(拆中盒)");
        rowField.createCell(4).setCellValue("可用库存(拆中盒)");
        rowField.createCell(5).setCellValue("占用库存");
        rowField.createCell(6).setCellValue("占用库存(拆中盒)");
        rowField.createCell(7).setCellValue("近90天销量");
        rowField.createCell(8).setCellValue("近30天销量");
        rowField.createCell(9).setCellValue("第-4周销量");
        rowField.createCell(10).setCellValue("第-3周销量");
        rowField.createCell(11).setCellValue("第-2周销量");
        rowField.createCell(12).setCellValue("第-1周销量");
        rowField.createCell(13).setCellValue("近90天销量（拆中盒）");
        rowField.createCell(14).setCellValue("近30天销量（拆中盒）");
        rowField.createCell(15).setCellValue("第-4周销量（拆中盒）");
        rowField.createCell(16).setCellValue("第-3周销量（拆中盒）");
        rowField.createCell(17).setCellValue("第-2周销量（拆中盒）");
        rowField.createCell(18).setCellValue("第-1周销量（拆中盒）");
        rowField.createCell(19).setCellValue("近14天平均销量");
        rowField.createCell(20).setCellValue("近14天平均销量（拆中盒）");
        rowField.createCell(21).setCellValue("近2周销售趋势");
        rowField.createCell(22).setCellValue("全部库存周转（近90天销量）");
        rowField.createCell(23).setCellValue("全部库存周转（近30天销量）");
        rowField.createCell(24).setCellValue("可用库存周转（近90天销量）");
        rowField.createCell(25).setCellValue("可用库存周转（近30天销量）");
        rowField.createCell(26).setCellValue("全部库存周转-拆中盒（近90天销量）");
        rowField.createCell(27).setCellValue("全部库存周转-拆中盒（近30天销量）");
        rowField.createCell(28).setCellValue("可用库存周转-拆中盒（近90天销量）");
        rowField.createCell(29).setCellValue("可用库存周转-拆中盒（近30天销量）");
        rowField.createCell(30).setCellValue("全部库存预警（近30天销量）");
        rowField.createCell(31).setCellValue("可用库存预警（近30天销量）");
        rowField.createCell(32).setCellValue("全部库存预警-拆中盒（近30天销量）");
        rowField.createCell(33).setCellValue("可用库存预警-拆中盒（近30天销量）");
        rowField.createCell(34).setCellValue("累计进货");
        rowField.createCell(35).setCellValue("累计进货（拆中盒）");
        rowField.createCell(36).setCellValue("累计销售");
        rowField.createCell(37).setCellValue("累计销售（拆中盒）");
        rowField.createCell(38).setCellValue("采购次数(测试中)");
        rowField.createCell(39).setCellValue("最后一次采购量");
        rowField.createCell(40).setCellValue("最后一次采购量（拆中盒）");
        rowField.createCell(41).setCellValue("未到货");
        rowField.createCell(42).setCellValue("未到货（拆中盒）");
        int totalSale, totalSaleSplit;
        double qty, avbQty, qtySplit, avbQtySplit, qtyQuarter, qtyMonth, qtyWeek4, qtyWeek3, qtyWeek2, qtyWeek1;
        double qtyQuarterSplit, qtyMonthSplit, qtyWeekSplit4, qtyWeekSplit3, qtyWeekSplit2, qtyWeekSplit1;
        int count1 = 2;
        while (resultSet.next()) {
            SXSSFRow row = sheet.createRow(count1);
            row.createCell(0).setCellValue(resultSet.getString("product_source"));
            totalSale = resultSet.getInt("total_sale");
            totalSaleSplit = resultSet.getInt("total_sale_split");

            qty = resultSet.getDouble("qty");
            SXSSFCell cell1 = row.createCell(1);
            cell1.setCellStyle(numberCellStyle);
            cell1.setCellValue(qty);

            avbQty = resultSet.getDouble("avb_qty");
            SXSSFCell cell2 = row.createCell(2);
            cell2.setCellStyle(numberCellStyle);
            cell2.setCellValue(avbQty);

            qtySplit = resultSet.getDouble("qty_split");
            SXSSFCell cell3 = row.createCell(3);
            cell3.setCellStyle(numberCellStyle);
            cell3.setCellValue(qtySplit);

            avbQtySplit = resultSet.getDouble("avb_qty_split");
            SXSSFCell cell4 = row.createCell(4);
            cell4.setCellStyle(numberCellStyle);
            cell4.setCellValue(avbQtySplit);

            SXSSFCell cell5 = row.createCell(5);
            cell5.setCellStyle(numberCellStyle);
            cell5.setCellValue(qty - avbQty);
            SXSSFCell cell6 = row.createCell(6);
            cell6.setCellStyle(numberCellStyle);
            cell6.setCellValue(qtySplit - avbQtySplit);

            qtyQuarter = resultSet.getDouble("qty_quarter");
            SXSSFCell cell7 = row.createCell(7);
            cell7.setCellStyle(numberCellStyle);
            cell7.setCellValue(qtyQuarter);

            qtyMonth = resultSet.getDouble("qty_month");
            SXSSFCell cell8 = row.createCell(8);
            cell8.setCellStyle(numberCellStyle);
            cell8.setCellValue(qtyMonth);

            qtyWeek4 = resultSet.getDouble("qty_week4");
            SXSSFCell cell9 = row.createCell(9);
            cell9.setCellStyle(numberCellStyle);
            cell9.setCellValue(qtyWeek4);

            qtyWeek3 = resultSet.getDouble("qty_week3");
            SXSSFCell cell10 = row.createCell(10);
            cell10.setCellStyle(numberCellStyle);
            cell10.setCellValue(qtyWeek3);

            qtyWeek2 = resultSet.getDouble("qty_week2");
            SXSSFCell cell11 = row.createCell(11);
            cell11.setCellStyle(numberCellStyle);
            cell11.setCellValue(qtyWeek2);

            qtyWeek1 = resultSet.getDouble("qty_week1");
            SXSSFCell cell12 = row.createCell(12);
            cell2.setCellStyle(numberCellStyle);
            cell12.setCellValue(qtyWeek1);

            qtyQuarterSplit = resultSet.getDouble("qty_quarter_split");
            SXSSFCell cell13 = row.createCell(13);
            cell13.setCellStyle(numberCellStyle);
            cell13.setCellValue(qtyQuarterSplit);

            qtyMonthSplit = resultSet.getDouble("qty_month_split");
            SXSSFCell cell14 = row.createCell(14);
            cell14.setCellStyle(numberCellStyle);
            cell14.setCellValue(qtyMonthSplit);

            qtyWeekSplit4 = resultSet.getDouble("qty_week_split4");
            SXSSFCell cell15 = row.createCell(15);
            cell15.setCellStyle(numberCellStyle);
            cell15.setCellValue(qtyWeekSplit4);

            qtyWeekSplit3 = resultSet.getDouble("qty_week_split3");
            SXSSFCell cell16 = row.createCell(16);
            cell16.setCellStyle(numberCellStyle);
            cell16.setCellValue(qtyWeekSplit3);

            qtyWeekSplit2 = resultSet.getDouble("qty_week_split2");
            SXSSFCell cell17 = row.createCell(17);
            cell17.setCellStyle(numberCellStyle);
            cell17.setCellValue(qtyWeekSplit2);

            qtyWeekSplit1 = resultSet.getDouble("qty_week_split1");
            SXSSFCell cell18 = row.createCell(18);
            cell18.setCellStyle(numberCellStyle);
            cell18.setCellValue(qtyWeekSplit1);


            row.createCell(19).setCellValue(Util.mapNumber((qtyWeek1 + qtyWeek2) / 14d));
            row.createCell(20).setCellValue(Util.mapNumber((qtyWeekSplit1 + qtyWeekSplit2) / 14d));
            row.createCell(21).setCellValue(Util.trend((int) qtyWeek1, (int) qtyWeek2));
            if (qtyQuarter != 0) {
                row.createCell(22).setCellValue(Util.mapNumber(qty / qtyQuarter * 90d));
                row.createCell(24).setCellValue(Util.mapNumber(avbQty / qtyQuarter * 90d));
            }
            if (qtyMonth != 0) {
                row.createCell(23).setCellValue(Util.mapNumber(qty / qtyMonth * 30d));
                row.createCell(25).setCellValue(Util.mapNumber(avbQty / qtyMonth * 30d));
            }
            if (qtyQuarterSplit != 0) {
                row.createCell(26).setCellValue(Util.mapNumber(qtySplit / qtyQuarterSplit * 90d));
                row.createCell(28).setCellValue(Util.mapNumber(avbQtySplit / qtyQuarterSplit * 90d));
            }
            if (qtyMonthSplit != 0) {
                row.createCell(27).setCellValue(Util.mapNumber(qtySplit / qtyMonthSplit * 30d));
                row.createCell(29).setCellValue(Util.mapNumber(avbQtySplit / qtyMonthSplit * 30d));
            }
            row.createCell(30).setCellValue(Util.stockStatus((int) qty, (int) qtyMonth, 30, now, now));
            row.createCell(31).setCellValue(Util.stockStatus((int) avbQty, (int) qtyMonth, 30, now, now));
            row.createCell(32).setCellValue(Util.stockStatus((int) qtySplit, (int) qtyMonthSplit, 30, now, now));
            row.createCell(33).setCellValue(Util.stockStatus((int) avbQtySplit, (int) qtyMonthSplit, 30, now, now));

            SXSSFCell cell34 = row.createCell(34);
            cell34.setCellStyle(numberCellStyle);
            cell34.setCellValue(resultSet.getInt("total_instock"));
            SXSSFCell cell35 = row.createCell(35);
            cell35.setCellStyle(numberCellStyle);
            cell35.setCellValue(resultSet.getInt("total_instock_split"));

            SXSSFCell cell36 = row.createCell(36);
            cell36.setCellStyle(numberCellStyle);
            cell36.setCellValue(totalSale);
            SXSSFCell cell37 = row.createCell(37);
            cell37.setCellStyle(numberCellStyle);
            cell37.setCellValue(totalSaleSplit);

            SXSSFCell cell38 = row.createCell(38);
            cell38.setCellStyle(numberCellStyle);
            cell38.setCellValue(resultSet.getInt("buy_times"));

            SXSSFCell cell39 = row.createCell(39);
            cell39.setCellStyle(numberCellStyle);
            cell39.setCellValue(resultSet.getInt("last_buy"));
            SXSSFCell cell40 = row.createCell(40);
            cell40.setCellStyle(numberCellStyle);
            cell40.setCellValue(resultSet.getInt("last_buy_split"));

            SXSSFCell cell41 = row.createCell(41);
            cell41.setCellStyle(numberCellStyle);
            cell41.setCellValue(resultSet.getInt("future"));
            SXSSFCell cell42 = row.createCell(42);
            cell42.setCellStyle(numberCellStyle);
            cell42.setCellValue(resultSet.getInt("future_split"));

            count1++;
        }
        CellRangeAddress range = new CellRangeAddress(1, sheet.getLastRowNum(), 0, 42);
        sheet.setAutoFilter(range);
        ps.close();
    }
}
