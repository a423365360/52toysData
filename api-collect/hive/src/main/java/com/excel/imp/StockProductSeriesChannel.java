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

public class StockProductSeriesChannel implements ExcelSheet {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public StockProductSeriesChannel(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
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
        rowField.createCell(4).setCellValue("渠道");
        rowField.createCell(5).setCellValue("发售日期");
        rowField.createCell(6).setCellValue("货龄");
        rowField.createCell(7).setCellValue("全部库存");
        rowField.createCell(8).setCellValue("可用库存");
        rowField.createCell(9).setCellValue("全部库存(拆中盒)");
        rowField.createCell(10).setCellValue("可用库存(拆中盒)");
        rowField.createCell(11).setCellValue("占用库存");
        rowField.createCell(12).setCellValue("占用库存(拆中盒)");
        rowField.createCell(13).setCellValue("近90天销量");
        rowField.createCell(14).setCellValue("近30天销量");
        rowField.createCell(15).setCellValue("第-4周销量");
        rowField.createCell(16).setCellValue("第-3周销量");
        rowField.createCell(17).setCellValue("第-2周销量");
        rowField.createCell(18).setCellValue("第-1周销量");
        rowField.createCell(19).setCellValue("近90天销量（拆中盒）");
        rowField.createCell(20).setCellValue("近30天销量（拆中盒）");
        rowField.createCell(21).setCellValue("第-4周销量（拆中盒）");
        rowField.createCell(22).setCellValue("第-3周销量（拆中盒）");
        rowField.createCell(23).setCellValue("第-2周销量（拆中盒）");
        rowField.createCell(24).setCellValue("第-1周销量（拆中盒）");
        rowField.createCell(25).setCellValue("近14天平均销量");
        rowField.createCell(26).setCellValue("近14天平均销量（拆中盒）");
        rowField.createCell(27).setCellValue("近2周销售趋势");
        rowField.createCell(28).setCellValue("全部库存周转（近90天销量）");
        rowField.createCell(29).setCellValue("全部库存周转（近30天销量）");
        rowField.createCell(30).setCellValue("可用库存周转（近90天销量）");
        rowField.createCell(31).setCellValue("可用库存周转（近30天销量）");
        rowField.createCell(32).setCellValue("全部库存周转-拆中盒（近90天销量）");
        rowField.createCell(33).setCellValue("全部库存周转-拆中盒（近30天销量）");
        rowField.createCell(34).setCellValue("可用库存周转-拆中盒（近90天销量）");
        rowField.createCell(35).setCellValue("可用库存周转-拆中盒（近30天销量）");
        rowField.createCell(36).setCellValue("全部库存预警（近30天销量）");
        rowField.createCell(37).setCellValue("可用库存预警（近30天销量）");
        rowField.createCell(38).setCellValue("全部库存预警-拆中盒（近30天销量）");
        rowField.createCell(39).setCellValue("可用库存预警-拆中盒（近30天销量）");
        rowField.createCell(40).setCellValue("累计进货");
        rowField.createCell(41).setCellValue("累计进货（拆中盒）");
        rowField.createCell(42).setCellValue("累计销售");
        rowField.createCell(43).setCellValue("累计销售（拆中盒）");
        rowField.createCell(44).setCellValue("采购次数(测试中)");
        rowField.createCell(45).setCellValue("最后一次采购量");
        rowField.createCell(46).setCellValue("最后一次采购量（拆中盒）");
        rowField.createCell(47).setCellValue("未到货");
        rowField.createCell(48).setCellValue("未到货（拆中盒）");
        int totalSale, totalSaleSplit, matchFlag;
        double qty, avbQty, qtySplit, avbQtySplit, qtyQuarter, qtyMonth, qtyWeek4, qtyWeek3, qtyWeek2, qtyWeek1;
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
            row.createCell(4).setCellValue(resultSet.getString("channel"));
            Date saleDate = resultSet.getDate("sale_date");
            if (saleDate != null
                    && saleDate.before(StockConstant.MAX_DATE)
                    && productSource != null
                    && !(saleDate.after(StockConstant.HIDE_DATE) && matchFlag != 1 && productSource.equals("自主研发"))) {
                SXSSFCell cell7 = row.createCell(5);
                cell7.setCellStyle(dateCellStyle);
                cell7.setCellValue(saleDate);
            }
            row.createCell(6).setCellValue(Util.mapStockAge(saleDate));
            totalSale = resultSet.getInt("total_sale");
            totalSaleSplit = resultSet.getInt("total_sale_split");

            qty = resultSet.getDouble("qty");
            SXSSFCell cell7 = row.createCell(7);
            cell7.setCellStyle(numberCellStyle);
            cell7.setCellValue(qty);

            avbQty = resultSet.getDouble("avb_qty");
            SXSSFCell cell8 = row.createCell(8);
            cell8.setCellStyle(numberCellStyle);
            cell8.setCellValue(avbQty);

            qtySplit = resultSet.getDouble("qty_split");
            SXSSFCell cell9= row.createCell(9);
            cell9.setCellStyle(numberCellStyle);
            cell9.setCellValue(qtySplit);

            avbQtySplit = resultSet.getDouble("avb_qty_split");
            SXSSFCell cell10= row.createCell(10);
            cell10.setCellStyle(numberCellStyle);
            cell10.setCellValue(avbQtySplit);

            SXSSFCell cell11= row.createCell(11);
            cell11.setCellStyle(numberCellStyle);
            cell11.setCellValue(qty - avbQty);
            SXSSFCell cell12= row.createCell(12);
            cell12.setCellStyle(numberCellStyle);
            cell12.setCellValue(qtySplit - avbQtySplit);

            qtyQuarter = resultSet.getDouble("qty_quarter");
            SXSSFCell cell13= row.createCell(13);
            cell13.setCellStyle(numberCellStyle);
            cell13.setCellValue(qtyQuarter);

            qtyMonth = resultSet.getDouble("qty_month");
            SXSSFCell cell14= row.createCell(14);
            cell14.setCellStyle(numberCellStyle);
            cell14.setCellValue(qtyMonth);

            qtyWeek4 = resultSet.getDouble("qty_week4");
            SXSSFCell cell15= row.createCell(15);
            cell15.setCellStyle(numberCellStyle);
            cell15.setCellValue(qtyWeek4);

            qtyWeek3 = resultSet.getDouble("qty_week3");
            SXSSFCell cell16= row.createCell(16);
            cell16.setCellStyle(numberCellStyle);
            cell16.setCellValue(qtyWeek3);

            qtyWeek2 = resultSet.getDouble("qty_week2");
            SXSSFCell cell17= row.createCell(17);
            cell17.setCellStyle(numberCellStyle);
            cell17.setCellValue(qtyWeek2);

            qtyWeek1 = resultSet.getDouble("qty_week1");
            SXSSFCell cell18= row.createCell(18);
            cell18.setCellStyle(numberCellStyle);
            cell18.setCellValue(qtyWeek1);

            qtyQuarterSplit = resultSet.getDouble("qty_quarter_split");
            SXSSFCell cell19= row.createCell(19);
            cell19.setCellStyle(numberCellStyle);
            cell19.setCellValue(qtyQuarterSplit);

            qtyMonthSplit = resultSet.getDouble("qty_month_split");
            SXSSFCell cell20= row.createCell(20);
            cell20.setCellStyle(numberCellStyle);
            cell20.setCellValue(qtyMonthSplit);

            qtyWeekSplit4 = resultSet.getDouble("qty_week_split4");
            SXSSFCell cell21= row.createCell(21);
            cell21.setCellStyle(numberCellStyle);
            cell21.setCellValue(qtyWeekSplit4);

            qtyWeekSplit3 = resultSet.getDouble("qty_week_split3");
            SXSSFCell cell22= row.createCell(22);
            cell22.setCellStyle(numberCellStyle);
            cell22.setCellValue(qtyWeekSplit3);

            qtyWeekSplit2 = resultSet.getDouble("qty_week_split2");
            SXSSFCell cell23= row.createCell(23);
            cell23.setCellStyle(numberCellStyle);
            cell23.setCellValue(qtyWeekSplit2);

            qtyWeekSplit1 = resultSet.getDouble("qty_week_split1");
            SXSSFCell cell24= row.createCell(24);
            cell24.setCellStyle(numberCellStyle);
            cell24.setCellValue(qtyWeekSplit1);

            row.createCell(25).setCellValue(Util.mapNumber((qtyWeek1 + qtyWeek2) / 14d));
            row.createCell(26).setCellValue(Util.mapNumber((qtyWeekSplit1 + qtyWeekSplit2) / 14d));
            row.createCell(27).setCellValue(Util.trend((int) qtyWeek1, (int) qtyWeek2));
            if (qtyQuarter != 0) {
                row.createCell(28).setCellValue(Util.mapNumber(qty / qtyQuarter * 90d));
                row.createCell(30).setCellValue(Util.mapNumber(avbQty / qtyQuarter * 90d));
            }
            if (qtyMonth != 0) {
                row.createCell(29).setCellValue(Util.mapNumber(qty / qtyMonth * 30d));
                row.createCell(31).setCellValue(Util.mapNumber(avbQty / qtyMonth * 30d));
            }
            if (qtyQuarterSplit != 0) {
                row.createCell(32).setCellValue(Util.mapNumber(qtySplit / qtyQuarterSplit * 90d));
                row.createCell(34).setCellValue(Util.mapNumber(avbQtySplit / qtyQuarterSplit * 90d));
            }
            if (qtyMonthSplit != 0) {
                row.createCell(33).setCellValue(Util.mapNumber(qtySplit / qtyMonthSplit * 30d));
                row.createCell(35).setCellValue(Util.mapNumber(avbQtySplit / qtyMonthSplit * 30d));
            }
            row.createCell(36).setCellValue(Util.stockStatus((int) qty, (int) qtyMonth, 30, saleDate, now));
            row.createCell(37).setCellValue(Util.stockStatus((int) avbQty, (int) qtyMonth, 30, saleDate, now));
            row.createCell(38).setCellValue(Util.stockStatus((int) qtySplit, (int) qtyMonthSplit, 30, saleDate, now));
            row.createCell(39).setCellValue(Util.stockStatus((int) avbQtySplit, (int) qtyMonthSplit, 30, saleDate, now));

            SXSSFCell cell40 = row.createCell(40);
            cell40.setCellStyle(numberCellStyle);
            cell40.setCellValue(resultSet.getInt("total_instock"));
            SXSSFCell cell41 = row.createCell(41);
            cell41.setCellStyle(numberCellStyle);
            cell41.setCellValue(resultSet.getInt("total_instock_split"));

            SXSSFCell cell42 = row.createCell(42);
            cell42.setCellStyle(numberCellStyle);
            cell42.setCellValue(totalSale);
            SXSSFCell cell43 = row.createCell(43);
            cell43.setCellStyle(numberCellStyle);
            cell43.setCellValue(totalSaleSplit);

            SXSSFCell cell44 = row.createCell(44);
            cell44.setCellStyle(numberCellStyle);
            cell44.setCellValue(resultSet.getInt("buy_times"));

            SXSSFCell cell45 = row.createCell(45);
            cell45.setCellStyle(numberCellStyle);
            cell45.setCellValue(resultSet.getInt("last_buy"));
            SXSSFCell cell46 = row.createCell(46);
            cell46.setCellStyle(numberCellStyle);
            cell46.setCellValue(resultSet.getInt("last_buy_split"));

            SXSSFCell cell47 = row.createCell(47);
            cell47.setCellStyle(numberCellStyle);
            cell47.setCellValue(resultSet.getInt("future"));

            SXSSFCell cell48 = row.createCell(48);
            cell48.setCellStyle(numberCellStyle);
            cell48.setCellValue(resultSet.getInt("future_split"));

            count5++;
        }
        CellRangeAddress range = new CellRangeAddress(1, sheet.getLastRowNum(), 0, 48);
        sheet.setAutoFilter(range);
        ps.close();
    }
}
