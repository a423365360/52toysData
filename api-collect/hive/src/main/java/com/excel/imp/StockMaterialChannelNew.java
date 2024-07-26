package com.excel.imp;

import cn.hutool.core.date.DateTime;
import com.constant.StockConstant;
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

public class StockMaterialChannelNew implements ExcelSheet {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public StockMaterialChannelNew(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
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

        CellStyle percentStyle = xssfWorkbook.createCellStyle();
        percentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));

        // 使用 DataFormat 对象创建一个数字格式
        CellStyle periodStyle = xssfWorkbook.createCellStyle();
        periodStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));

        // 物料 - 渠道
        SXSSFSheet sheet = xssfWorkbook.createSheet(table);
        PreparedStatement ps = hiveConnection.prepareStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        sheet.createRow(0).createCell(0).setCellValue(table);

        SXSSFRow rowField = sheet.createRow(1);
        rowField.createCell(0).setCellValue("产品来源");
        rowField.createCell(1).setCellValue("产品线");
        rowField.createCell(2).setCellValue("IP细分");
        rowField.createCell(3).setCellValue("产品系列");
        // 物料属性
        rowField.createCell(4).setCellValue("物料编码");
        rowField.createCell(5).setCellValue("物料名称");
        rowField.createCell(6).setCellValue("是否混装");
        rowField.createCell(7).setCellValue("售价");
        rowField.createCell(8).setCellValue("成本");
        rowField.createCell(9).setCellValue("成本(含税)");
        rowField.createCell(10).setCellValue("盒规格");
        //
        rowField.createCell(11).setCellValue("渠道");
        rowField.createCell(12).setCellValue("发售日期");
        rowField.createCell(13).setCellValue("货龄");
        rowField.createCell(14).setCellValue("全部库存");
        rowField.createCell(15).setCellValue("可用库存");
        rowField.createCell(16).setCellValue("全部库存(拆中盒)");
        rowField.createCell(17).setCellValue("可用库存(拆中盒)");
        rowField.createCell(18).setCellValue("占用库存");
        rowField.createCell(19).setCellValue("占用库存(拆中盒)");
        rowField.createCell(20).setCellValue("近90天销量");
        rowField.createCell(21).setCellValue("近30天销量");
        rowField.createCell(22).setCellValue("第-4周销量");
        rowField.createCell(23).setCellValue("第-3周销量");
        rowField.createCell(24).setCellValue("第-2周销量");
        rowField.createCell(25).setCellValue("第-1周销量");
        rowField.createCell(26).setCellValue("近90天销量（拆中盒）");
        rowField.createCell(27).setCellValue("近30天销量（拆中盒）");
        rowField.createCell(28).setCellValue("第-4周销量（拆中盒）");
        rowField.createCell(29).setCellValue("第-3周销量（拆中盒）");
        rowField.createCell(30).setCellValue("第-2周销量（拆中盒）");
        rowField.createCell(31).setCellValue("第-1周销量（拆中盒）");
        rowField.createCell(32).setCellValue("近14天平均销量");
        rowField.createCell(33).setCellValue("近14天平均销量（拆中盒）");
        rowField.createCell(34).setCellValue("近2周销售趋势");
        rowField.createCell(35).setCellValue("全部库存周转（近90天销量）");
        rowField.createCell(36).setCellValue("全部库存周转（近30天销量）");
        rowField.createCell(37).setCellValue("可用库存周转（近90天销量）");
        rowField.createCell(38).setCellValue("可用库存周转（近30天销量）");
        rowField.createCell(39).setCellValue("全部库存周转-拆中盒（近90天销量）");
        rowField.createCell(40).setCellValue("全部库存周转-拆中盒（近30天销量）");
        rowField.createCell(41).setCellValue("可用库存周转-拆中盒（近90天销量）");
        rowField.createCell(42).setCellValue("可用库存周转-拆中盒（近30天销量）");
        rowField.createCell(43).setCellValue("全部库存预警（近30天销量）");
        rowField.createCell(44).setCellValue("可用库存预警（近30天销量）");
        rowField.createCell(45).setCellValue("全部库存预警-拆中盒（近30天销量）");
        rowField.createCell(46).setCellValue("可用库存预警-拆中盒（近30天销量）");
        rowField.createCell(47).setCellValue("累计进货");
        rowField.createCell(48).setCellValue("累计进货（拆中盒）");
        rowField.createCell(49).setCellValue("累计销售");
        rowField.createCell(50).setCellValue("累计销售（拆中盒）");
        rowField.createCell(51).setCellValue("采购次数(测试中)");
        rowField.createCell(52).setCellValue("最后一次采购量");
        rowField.createCell(53).setCellValue("最后一次采购量（拆中盒）");
        rowField.createCell(54).setCellValue("未到货");
        rowField.createCell(55).setCellValue("未到货（拆中盒）");
        int totalSale, totalSaleSplit, matchFlag;
        double qty, avbQty, qtySplit, avbQtySplit, qtyQuarter, qtyMonth, qtyWeek4, qtyWeek3, qtyWeek2, qtyWeek1, cost;
        double qtyQuarterSplit, qtyMonthSplit, qtyWeekSplit4, qtyWeekSplit3, qtyWeekSplit2, qtyWeekSplit1;
        String productSource;
        int count5 = 2;
        while (resultSet.next()) {
            SXSSFRow row = sheet.createRow(count5);
            productSource = resultSet.getString("product_source");
            matchFlag = resultSet.getInt("match_flag");
            row.createCell(0).setCellValue(productSource);
            row.createCell(1).setCellValue(resultSet.getString("product_line"));
            row.createCell(2).setCellValue(resultSet.getString("ip_sub"));
            row.createCell(3).setCellValue(resultSet.getString("product_series"));

            row.createCell(4).setCellValue(resultSet.getString("material_number"));
            row.createCell(5).setCellValue(resultSet.getString("material_name"));
            row.createCell(6).setCellValue(resultSet.getBoolean("isHun"));
            row.createCell(7).setCellValue(resultSet.getDouble("sale_price"));
            cost = resultSet.getDouble("cost");
            row.createCell(8).setCellValue(cost);
            row.createCell(9).setCellValue(cost * 1.13);
            row.createCell(10).setCellValue(resultSet.getInt("pack_model"));


            row.createCell(11).setCellValue(resultSet.getString("channel"));
            Date saleDate = resultSet.getDate("sale_date");

            SXSSFCell celld = row.createCell(12);
            celld.setCellStyle(dateCellStyle);
            celld.setCellValue(saleDate);

            row.createCell(13).setCellValue(Util.mapStockAge(saleDate));
            totalSale = resultSet.getInt("total_sale");
            totalSaleSplit = resultSet.getInt("total_sale_split");

            qty = resultSet.getDouble("qty");
            SXSSFCell cell1 = row.createCell(14);
            cell1.setCellStyle(numberCellStyle);
            cell1.setCellValue(qty);

            avbQty = resultSet.getDouble("avb_qty");
            SXSSFCell cell2 = row.createCell(15);
            cell2.setCellStyle(numberCellStyle);
            cell2.setCellValue(avbQty);

            qtySplit = resultSet.getDouble("qty_split");
            SXSSFCell cell3 = row.createCell(16);
            cell3.setCellStyle(numberCellStyle);
            cell3.setCellValue(qtySplit);

            avbQtySplit = resultSet.getDouble("avb_qty_split");
            SXSSFCell cell4 = row.createCell(17);
            cell4.setCellStyle(numberCellStyle);
            cell4.setCellValue(avbQtySplit);

            SXSSFCell cell5 = row.createCell(18);
            cell5.setCellStyle(numberCellStyle);
            cell5.setCellValue(qty - avbQty);
            SXSSFCell cell6 = row.createCell(19);
            cell6.setCellStyle(numberCellStyle);
            cell6.setCellValue(qtySplit - avbQtySplit);

            qtyQuarter = resultSet.getDouble("qty_quarter");
            SXSSFCell cell7 = row.createCell(20);
            cell7.setCellStyle(numberCellStyle);
            cell7.setCellValue(qtyQuarter);

            qtyMonth = resultSet.getDouble("qty_month");
            SXSSFCell cell8 = row.createCell(21);
            cell8.setCellStyle(numberCellStyle);
            cell8.setCellValue(qtyMonth);

            qtyWeek4 = resultSet.getDouble("qty_week4");
            SXSSFCell cell9 = row.createCell(22);
            cell9.setCellStyle(numberCellStyle);
            cell9.setCellValue(qtyWeek4);

            qtyWeek3 = resultSet.getDouble("qty_week3");
            SXSSFCell cell10 = row.createCell(23);
            cell10.setCellStyle(numberCellStyle);
            cell10.setCellValue(qtyWeek3);

            qtyWeek2 = resultSet.getDouble("qty_week2");
            SXSSFCell cell11 = row.createCell(24);
            cell11.setCellStyle(numberCellStyle);
            cell11.setCellValue(qtyWeek2);

            qtyWeek1 = resultSet.getDouble("qty_week1");
            SXSSFCell cell12 = row.createCell(25);
            cell2.setCellStyle(numberCellStyle);
            cell12.setCellValue(qtyWeek1);

            qtyQuarterSplit = resultSet.getDouble("qty_quarter_split");
            SXSSFCell cell13 = row.createCell(26);
            cell13.setCellStyle(numberCellStyle);
            cell13.setCellValue(qtyQuarterSplit);

            qtyMonthSplit = resultSet.getDouble("qty_month_split");
            SXSSFCell cell14 = row.createCell(27);
            cell14.setCellStyle(numberCellStyle);
            cell14.setCellValue(qtyMonthSplit);

            qtyWeekSplit4 = resultSet.getDouble("qty_week_split4");
            SXSSFCell cell15 = row.createCell(28);
            cell15.setCellStyle(numberCellStyle);
            cell15.setCellValue(qtyWeekSplit4);

            qtyWeekSplit3 = resultSet.getDouble("qty_week_split3");
            SXSSFCell cell16 = row.createCell(29);
            cell16.setCellStyle(numberCellStyle);
            cell16.setCellValue(qtyWeekSplit3);

            qtyWeekSplit2 = resultSet.getDouble("qty_week_split2");
            SXSSFCell cell17 = row.createCell(30);
            cell17.setCellStyle(numberCellStyle);
            cell17.setCellValue(qtyWeekSplit2);

            qtyWeekSplit1 = resultSet.getDouble("qty_week_split1");
            SXSSFCell cell18 = row.createCell(31);
            cell18.setCellStyle(numberCellStyle);
            cell18.setCellValue(qtyWeekSplit1);

            row.createCell(32).setCellValue(Util.mapNumber((qtyWeek1 + qtyWeek2) / 14d));
            row.createCell(33).setCellValue(Util.mapNumber((qtyWeekSplit1 + qtyWeekSplit2) / 14d));
            row.createCell(34).setCellValue(Util.trend((int) qtyWeek1, (int) qtyWeek2));
            if (qtyQuarter != 0) {
                row.createCell(35).setCellValue(Util.mapNumber(qty / qtyQuarter * 90d));
                row.createCell(37).setCellValue(Util.mapNumber(avbQty / qtyQuarter * 90d));
            }
            if (qtyMonth != 0) {
                row.createCell(36).setCellValue(Util.mapNumber(qty / qtyMonth * 30d));
                row.createCell(38).setCellValue(Util.mapNumber(avbQty / qtyMonth * 30d));
            }
            if (qtyQuarterSplit != 0) {
                row.createCell(39).setCellValue(Util.mapNumber(qtySplit / qtyQuarterSplit * 90d));
                row.createCell(41).setCellValue(Util.mapNumber(avbQtySplit / qtyQuarterSplit * 90d));
            }
            if (qtyMonthSplit != 0) {
                row.createCell(40).setCellValue(Util.mapNumber(qtySplit / qtyMonthSplit * 30d));
                row.createCell(42).setCellValue(Util.mapNumber(avbQtySplit / qtyMonthSplit * 30d));
            }
            row.createCell(43).setCellValue(Util.stockStatus((int) qty, (int) qtyMonth, 30, saleDate, now));
            row.createCell(44).setCellValue(Util.stockStatus((int) avbQty, (int) qtyMonth, 30, saleDate, now));
            row.createCell(45).setCellValue(Util.stockStatus((int) qtySplit, (int) qtyMonthSplit, 30, saleDate, now));
            row.createCell(46).setCellValue(Util.stockStatus((int) avbQtySplit, (int) qtyMonthSplit, 30, saleDate, now));

            SXSSFCell cell34 = row.createCell(47);
            cell34.setCellStyle(numberCellStyle);
            cell34.setCellValue(resultSet.getInt("total_instock"));
            SXSSFCell cell35 = row.createCell(48);
            cell35.setCellStyle(numberCellStyle);
            cell35.setCellValue(resultSet.getInt("total_instock_split"));

            SXSSFCell cell36 = row.createCell(49);
            cell36.setCellStyle(numberCellStyle);
            cell36.setCellValue(totalSale);
            SXSSFCell cell37 = row.createCell(50);
            cell37.setCellStyle(numberCellStyle);
            cell37.setCellValue(totalSaleSplit);

            SXSSFCell cell38 = row.createCell(51);
            cell38.setCellStyle(numberCellStyle);
            cell38.setCellValue(resultSet.getInt("buy_times"));

            SXSSFCell cell39 = row.createCell(52);
            cell39.setCellStyle(numberCellStyle);
            cell39.setCellValue(resultSet.getInt("last_buy"));
            SXSSFCell cell40 = row.createCell(53);
            cell40.setCellStyle(numberCellStyle);
            cell40.setCellValue(resultSet.getInt("last_buy_split"));

            SXSSFCell cell41 = row.createCell(54);
            cell41.setCellStyle(numberCellStyle);
            cell41.setCellValue(resultSet.getInt("future"));
            SXSSFCell cell42 = row.createCell(55);
            cell42.setCellStyle(numberCellStyle);
            cell42.setCellValue(resultSet.getInt("future_split"));

            count5++;
        }
        CellRangeAddress range = new CellRangeAddress(1, sheet.getLastRowNum(), 0, 55);
        sheet.setAutoFilter(range);
        ps.close();
    }
}
