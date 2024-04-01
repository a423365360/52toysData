package com.excel.imp;

import cn.hutool.core.date.DateTime;
import com.constant.StockConstant;
import com.excel.ExcelSheet;
import com.util.Util;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static cn.hutool.poi.excel.cell.CellUtil.setCellValue;

public class StockProductSeries implements ExcelSheet {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public StockProductSeries(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql) throws Exception {
        Date now = DateTime.now().toSqlDate();
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));

        CellStyle percentStyle = xssfWorkbook.createCellStyle();
        percentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));

        // 使用 DataFormat 对象创建一个数字格式
        CellStyle periodStyle = xssfWorkbook.createCellStyle();
        periodStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));

        // 产品系列
        SXSSFSheet sheet = xssfWorkbook.createSheet(table);
        PreparedStatement ps = hiveConnection.prepareStatement(sql);
        ResultSet resultSet = ps.executeQuery();

        SXSSFRow rowTitle = sheet.createRow(0);
        rowTitle.createCell(0).setCellValue(table);

        SXSSFRow rowField = sheet.createRow(1);
        rowField.createCell(0).setCellValue("产品来源");
        rowField.createCell(1).setCellValue("产品线");
        rowField.createCell(2).setCellValue("IP细分");
        rowField.createCell(3).setCellValue("产品系列");
        rowField.createCell(4).setCellValue("发售时间");
        rowField.createCell(5).setCellValue("货龄");
        rowField.createCell(6).setCellValue("全部库存");
        rowField.createCell(7).setCellValue("可用库存");
        rowField.createCell(8).setCellValue("全部库存(拆中盒)");
        rowField.createCell(9).setCellValue("可用库存(拆中盒)");
        rowField.createCell(10).setCellValue("占用库存");
        rowField.createCell(11).setCellValue("占用库存(拆中盒)");
        rowField.createCell(12).setCellValue("近90天销量");
        rowField.createCell(13).setCellValue("近30天销量");
        rowField.createCell(14).setCellValue("第-4周销量");
        rowField.createCell(15).setCellValue("第-3周销量");
        rowField.createCell(16).setCellValue("第-2周销量");
        rowField.createCell(17).setCellValue("第-1周销量");
        rowField.createCell(18).setCellValue("近90天销量（拆中盒）");
        rowField.createCell(19).setCellValue("近30天销量（拆中盒）");
        rowField.createCell(20).setCellValue("第-4周销量（拆中盒）");
        rowField.createCell(21).setCellValue("第-3周销量（拆中盒）");
        rowField.createCell(22).setCellValue("第-2周销量（拆中盒）");
        rowField.createCell(23).setCellValue("第-1周销量（拆中盒）");
        rowField.createCell(24).setCellValue("近14天平均销量");
        rowField.createCell(25).setCellValue("近14天平均销量（拆中盒）");
        rowField.createCell(26).setCellValue("近2周销售趋势");
        rowField.createCell(27).setCellValue("全部库存周转（近90天销量）");
        rowField.createCell(28).setCellValue("全部库存周转（近30天销量）");
        rowField.createCell(29).setCellValue("可用库存周转（近90天销量）");
        rowField.createCell(30).setCellValue("可用库存周转（近30天销量）");
        rowField.createCell(31).setCellValue("全部库存周转-拆中盒（近90天销量）");
        rowField.createCell(32).setCellValue("全部库存周转-拆中盒（近30天销量）");
        rowField.createCell(33).setCellValue("可用库存周转-拆中盒（近90天销量）");
        rowField.createCell(34).setCellValue("可用库存周转-拆中盒（近30天销量）");
        rowField.createCell(35).setCellValue("全部库存预警（近30天销量）");
        rowField.createCell(36).setCellValue("可用库存预警（近30天销量）");
        rowField.createCell(37).setCellValue("全部库存预警-拆中盒（近30天销量）");
        rowField.createCell(38).setCellValue("可用库存预警-拆中盒（近30天销量）");
        rowField.createCell(39).setCellValue("累计进货");
        rowField.createCell(40).setCellValue("累计进货（拆中盒）");
        rowField.createCell(41).setCellValue("累计销售");
        rowField.createCell(42).setCellValue("累计销售（拆中盒）");
        rowField.createCell(43).setCellValue("采购次数(测试中)");
        rowField.createCell(44).setCellValue("最后一次采购量");
        rowField.createCell(45).setCellValue("最后一次采购量（拆中盒）");
        rowField.createCell(46).setCellValue("未到货");
        rowField.createCell(47).setCellValue("未到货（拆中盒）");
        int totalSale, totalSaleSplit, matchFlag;
        double qty, avbQty, qtySplit, avbQtySplit, qtyQuarter, qtyMonth, qtyWeek4, qtyWeek3, qtyWeek2, qtyWeek1;
        double qtyQuarterSplit, qtyMonthSplit, qtyWeekSplit4, qtyWeekSplit3, qtyWeekSplit2, qtyWeekSplit1;
        String productSource;
        int count = 2;
        while (resultSet.next()) {
            SXSSFRow row = sheet.createRow(count);
            productSource = resultSet.getString("product_source");
            matchFlag = resultSet.getInt("match_flag");
            row.createCell(0).setCellValue(productSource);
            row.createCell(1).setCellValue(resultSet.getString("product_line"));
            row.createCell(2).setCellValue(resultSet.getString("ip_sub"));
            row.createCell(3).setCellValue(resultSet.getString("product_series"));
            Date saleDate = resultSet.getDate("sale_date");
            if (saleDate != null
                    && saleDate.before(StockConstant.MAX_DATE)
                    && productSource != null
                    && !(saleDate.after(StockConstant.HIDE_DATE) && matchFlag != 1 && productSource.equals("自主研发"))) {
                SXSSFCell cell7 = row.createCell(4);
                cell7.setCellStyle(dateCellStyle);
                cell7.setCellValue(saleDate);
            }
            row.createCell(5).setCellValue(Util.mapStockAge(saleDate));
            totalSale = resultSet.getInt("total_sale");
            totalSaleSplit = resultSet.getInt("total_sale_split");
            qty = resultSet.getDouble("qty");
            row.createCell(6).setCellValue(qty);
            avbQty = resultSet.getDouble("avb_qty");
            row.createCell(7).setCellValue(avbQty);
            qtySplit = resultSet.getDouble("qty_split");
            row.createCell(8).setCellValue(qtySplit);
            avbQtySplit = resultSet.getDouble("avb_qty_split");
            row.createCell(9).setCellValue(avbQtySplit);
            row.createCell(10).setCellValue(qty - avbQty);
            row.createCell(11).setCellValue(qtySplit - avbQtySplit);
            qtyQuarter = resultSet.getDouble("qty_quarter");
            row.createCell(12).setCellValue(qtyQuarter);
            qtyMonth = resultSet.getDouble("qty_month");
            row.createCell(13).setCellValue(qtyMonth);
            qtyWeek4 = resultSet.getDouble("qty_week4");
            row.createCell(14).setCellValue(qtyWeek4);
            qtyWeek3 = resultSet.getDouble("qty_week3");
            row.createCell(15).setCellValue(qtyWeek3);
            qtyWeek2 = resultSet.getDouble("qty_week2");
            row.createCell(16).setCellValue(qtyWeek2);
            qtyWeek1 = resultSet.getDouble("qty_week1");
            row.createCell(17).setCellValue(qtyWeek1);
            qtyQuarterSplit = resultSet.getDouble("qty_quarter_split");
            row.createCell(18).setCellValue(qtyQuarterSplit);
            qtyMonthSplit = resultSet.getDouble("qty_month_split");
            row.createCell(19).setCellValue(qtyMonthSplit);
            qtyWeekSplit4 = resultSet.getDouble("qty_week_split4");
            row.createCell(20).setCellValue(qtyWeekSplit4);
            qtyWeekSplit3 = resultSet.getDouble("qty_week_split3");
            row.createCell(21).setCellValue(qtyWeekSplit3);
            qtyWeekSplit2 = resultSet.getDouble("qty_week_split2");
            row.createCell(22).setCellValue(qtyWeekSplit2);
            qtyWeekSplit1 = resultSet.getDouble("qty_week_split1");
            row.createCell(23).setCellValue(qtyWeekSplit1);
            row.createCell(24).setCellValue(Util.mapNumber((qtyWeek1 + qtyWeek2) / 14d));
            row.createCell(25).setCellValue(Util.mapNumber((qtyWeekSplit1 + qtyWeekSplit2) / 14d));
            row.createCell(26).setCellValue(Util.trend((int) qtyWeek1, (int) qtyWeek2));
            if (qtyQuarter != 0) {
                row.createCell(27).setCellValue(Util.mapNumber(qty / qtyQuarter * 90d));
                row.createCell(29).setCellValue(Util.mapNumber(avbQty / qtyQuarter * 90d));
            }
            if (qtyMonth != 0) {
                row.createCell(28).setCellValue(Util.mapNumber(qty / qtyMonth * 30d));
                row.createCell(30).setCellValue(Util.mapNumber(avbQty / qtyMonth * 30d));
            }
            if (qtyQuarterSplit != 0) {
                row.createCell(31).setCellValue(Util.mapNumber(qtySplit / qtyQuarterSplit * 90d));
                row.createCell(33).setCellValue(Util.mapNumber(avbQtySplit / qtyQuarterSplit * 90d));
            }
            if (qtyMonthSplit != 0) {
                row.createCell(32).setCellValue(Util.mapNumber(qtySplit / qtyMonthSplit * 30d));
                row.createCell(34).setCellValue(Util.mapNumber(avbQtySplit / qtyMonthSplit * 30d));
            }
            row.createCell(35).setCellValue(Util.stockStatus((int) qty, (int) qtyMonth, 30, saleDate, now));
            row.createCell(36).setCellValue(Util.stockStatus((int) avbQty, (int) qtyMonth, 30, saleDate, now));
            row.createCell(37).setCellValue(Util.stockStatus((int) avbQtySplit, (int) qtyMonthSplit, 30, saleDate, now));
            row.createCell(38).setCellValue(Util.stockStatus((int) qtySplit, (int) qtyMonthSplit, 30, saleDate, now));
            row.createCell(39).setCellValue(resultSet.getInt("total_instock"));
            row.createCell(40).setCellValue(resultSet.getInt("total_instock_split"));
            row.createCell(41).setCellValue(totalSale);
            row.createCell(42).setCellValue(totalSaleSplit);
            row.createCell(43).setCellValue(resultSet.getInt("buy_times"));
            row.createCell(44).setCellValue(resultSet.getInt("last_buy"));
            row.createCell(45).setCellValue(resultSet.getInt("last_buy_split"));
            row.createCell(46).setCellValue(resultSet.getInt("future"));
            row.createCell(47).setCellValue(resultSet.getInt("future_split"));
            count++;
        }

        ps.close();
    }
}
