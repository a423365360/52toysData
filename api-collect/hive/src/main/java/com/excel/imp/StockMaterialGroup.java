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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class StockMaterialGroup implements ExcelSheet {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public StockMaterialGroup(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
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

        // 仓库分组
        SXSSFSheet sheet = xssfWorkbook.createSheet(table);
        PreparedStatement ps = hiveConnection.prepareStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        sheet.createRow(0).createCell(0).setCellValue(table);

        SXSSFRow rowField = sheet.createRow(1);
        rowField.createCell(0).setCellValue("物料名称");
        rowField.createCell(1).setCellValue("物料编码");
        rowField.createCell(2).setCellValue("仓库分组");
        rowField.createCell(3).setCellValue("产品来源");
        rowField.createCell(4).setCellValue("产品线");
        rowField.createCell(5).setCellValue("产品系列");
        rowField.createCell(6).setCellValue("IP细分");
        rowField.createCell(7).setCellValue("售价");
        rowField.createCell(8).setCellValue("发售日期");
        rowField.createCell(9).setCellValue("货龄");
        rowField.createCell(10).setCellValue("成本");
        rowField.createCell(11).setCellValue("含税成本");
        rowField.createCell(12).setCellValue("库存");
        rowField.createCell(13).setCellValue("可用库存");
        rowField.createCell(14).setCellValue("库存(拆中盒)");
        rowField.createCell(15).setCellValue("可用库存(拆中盒)");
        rowField.createCell(16).setCellValue("占用库存");
        rowField.createCell(17).setCellValue("占用库存(拆中盒)");
        rowField.createCell(18).setCellValue("近90天销量");
        rowField.createCell(19).setCellValue("近30天销量");
        rowField.createCell(20).setCellValue("第-4周销量");
        rowField.createCell(21).setCellValue("第-3周销量");
        rowField.createCell(22).setCellValue("第-2周销量");
        rowField.createCell(23).setCellValue("第-1周销量");
        rowField.createCell(24).setCellValue("近90天销量（拆中盒）");
        rowField.createCell(25).setCellValue("近30天销量（拆中盒）");
        rowField.createCell(26).setCellValue("第-4周销量（拆中盒）");
        rowField.createCell(27).setCellValue("第-3周销量（拆中盒）");
        rowField.createCell(28).setCellValue("第-2周销量（拆中盒）");
        rowField.createCell(29).setCellValue("第-1周销量（拆中盒）");
        rowField.createCell(30).setCellValue("近14天平均销量");
        rowField.createCell(31).setCellValue("近14天平均销量（拆中盒）");
        rowField.createCell(32).setCellValue("近2周销售趋势");
        rowField.createCell(33).setCellValue("全部库存周转（近90天销量）");
        rowField.createCell(34).setCellValue("全部库存周转（近30天销量）");
        rowField.createCell(35).setCellValue("可用库存周转（近90天销量）");
        rowField.createCell(36).setCellValue("可用库存周转（近30天销量）");
        rowField.createCell(37).setCellValue("全部库存周转-拆中盒（近90天销量）");
        rowField.createCell(38).setCellValue("全部库存周转-拆中盒（近30天销量）");
        rowField.createCell(39).setCellValue("可用库存周转-拆中盒（近90天销量）");
        rowField.createCell(40).setCellValue("可用库存周转-拆中盒（近30天销量）");
        rowField.createCell(41).setCellValue("全部库存预警（近30天销量）");
        rowField.createCell(42).setCellValue("可用库存预警（近30天销量）");
        rowField.createCell(43).setCellValue("全部库存预警-拆中盒（近30天销量）");
        rowField.createCell(44).setCellValue("可用库存预警-拆中盒（近30天销量）");
        rowField.createCell(45).setCellValue("累计进货");
        rowField.createCell(46).setCellValue("累计进货（拆中盒）");
        rowField.createCell(47).setCellValue("累计销售");
        rowField.createCell(48).setCellValue("累计销售（拆中盒）");
        rowField.createCell(49).setCellValue("采购次数(测试中)");
        rowField.createCell(50).setCellValue("最后一次采购量");
        rowField.createCell(51).setCellValue("最后一次采购量（拆中盒）");
        rowField.createCell(52).setCellValue("未到货");
        rowField.createCell(53).setCellValue("未到货（拆中盒）");

        int middleBoxFlag, modelPack, qty, avbQty, qtySplit, avbQtySplit, matchFlag;
        int qtyQuarter, qtyMonth, qtyWeek4, qtyWeek3, qtyWeek2, qtyWeek1, totalSale, totalInstock, lastBuy, future;
        int qtyQuarterSplit, qtyMonthSplit, qtyWeekSplit4, qtyWeekSplit3, qtyWeekSplit2, qtyWeekSplit1, totalSaleSplit, totalInstockSplit, lastBuySplit, futureSplit;
        double cost, costWithTax;
        String productSource;

        int count5 = 2;
        while (resultSet.next()) {
            SXSSFRow row = sheet.createRow(count5);

            middleBoxFlag = resultSet.getInt("middle_box_flag");
            modelPack = resultSet.getInt("pack_model");
            qty = resultSet.getInt("qty");
            avbQty = resultSet.getInt("avb_qty");
            qtySplit = qty;
            avbQtySplit = avbQty;
            Date saleDate = resultSet.getDate("sale_date");
            qtyQuarter = resultSet.getInt("qty_quarter");
            qtyMonth = resultSet.getInt("qty_month");
            qtyWeek4 = resultSet.getInt("qty_week4");
            qtyWeek3 = resultSet.getInt("qty_week3");
            qtyWeek2 = resultSet.getInt("qty_week2");
            qtyWeek1 = resultSet.getInt("qty_week1");
            totalSale = resultSet.getInt("total_sale");
            totalInstock = resultSet.getInt("total_instock");
            lastBuy = resultSet.getInt("last_buy");
            future = resultSet.getInt("future");
            matchFlag = resultSet.getInt("match_flag");
            productSource = resultSet.getString("product_source");

            qtyQuarterSplit = qtyQuarter;
            qtyMonthSplit = qtyMonth;
            qtyWeekSplit1 = qtyWeek1;
            qtyWeekSplit2 = qtyWeek2;
            qtyWeekSplit3 = qtyWeek3;
            qtyWeekSplit4 = qtyWeek4;
            totalSaleSplit = totalSale;
            totalInstockSplit = totalInstock;
            lastBuySplit = lastBuy;
            futureSplit = future;
            cost = resultSet.getDouble("cost");
            costWithTax = cost * 1.13;
            if (middleBoxFlag == 1) {
                qtySplit *= modelPack;
                avbQtySplit *= modelPack;
                qtyQuarterSplit *= modelPack;
                qtyMonthSplit *= modelPack;
                qtyWeekSplit1 *= modelPack;
                qtyWeekSplit2 *= modelPack;
                qtyWeekSplit3 *= modelPack;
                qtyWeekSplit4 *= modelPack;
                totalSaleSplit *= modelPack;
                totalInstockSplit *= modelPack;
                lastBuySplit *= modelPack;
                futureSplit *= modelPack;
            }

            row.createCell(0).setCellValue(resultSet.getString("material_name"));
            row.createCell(1).setCellValue(resultSet.getString("material_number"));
            row.createCell(2).setCellValue(resultSet.getString("fgroup_name"));
            row.createCell(3).setCellValue(productSource);
            row.createCell(4).setCellValue(resultSet.getString("product_line"));
            row.createCell(5).setCellValue(resultSet.getString("product_series"));
            row.createCell(6).setCellValue(resultSet.getString("ip_sub"));
            row.createCell(7).setCellValue(resultSet.getDouble("sale_price"));
            if (saleDate != null
                    && saleDate.before(StockConstant.MAX_DATE)
                    && productSource != null
                    && !(saleDate.after(StockConstant.HIDE_DATE) && matchFlag != 1 && productSource.equals("自主研发"))) {
                SXSSFCell cell7 = row.createCell(8);
                cell7.setCellStyle(dateCellStyle);
                cell7.setCellValue(saleDate);
                row.createCell(9).setCellValue(Util.mapStockAge(saleDate));
            }
            row.createCell(10).setCellValue(cost);
            row.createCell(11).setCellValue(costWithTax);
            row.createCell(12).setCellValue(qty);
            row.createCell(13).setCellValue(avbQty);
            row.createCell(14).setCellValue(qtySplit);
            row.createCell(15).setCellValue(avbQtySplit);
            row.createCell(16).setCellValue(qty - avbQty);
            row.createCell(17).setCellValue(qtySplit - avbQtySplit);
            row.createCell(18).setCellValue(qtyQuarter);
            row.createCell(19).setCellValue(qtyMonth);
            row.createCell(20).setCellValue(qtyWeek4);
            row.createCell(21).setCellValue(qtyWeek3);
            row.createCell(22).setCellValue(qtyWeek2);
            row.createCell(23).setCellValue(qtyWeek1);
            row.createCell(24).setCellValue(qtyQuarterSplit);
            row.createCell(25).setCellValue(qtyMonthSplit);
            row.createCell(26).setCellValue(qtyWeekSplit4);
            row.createCell(27).setCellValue(qtyWeekSplit3);
            row.createCell(28).setCellValue(qtyWeekSplit2);
            row.createCell(29).setCellValue(qtyWeekSplit1);
            row.createCell(30).setCellValue(Util.mapNumber((qtyWeek1 + qtyWeek2) / 14d));
            row.createCell(31).setCellValue(Util.mapNumber((qtyWeekSplit1 + qtyWeekSplit2) / 14d));
            row.createCell(32).setCellValue(Util.trend(qtyWeek1, qtyWeek2));
            if (qtyQuarter != 0) {
                row.createCell(33).setCellValue(Util.mapNumber(qty / qtyQuarter * 90d));
                row.createCell(35).setCellValue(Util.mapNumber(avbQty / qtyQuarter * 90d));
            }
            if (qtyMonth != 0) {
                row.createCell(34).setCellValue(Util.mapNumber(qty / qtyMonth * 30d));
                row.createCell(36).setCellValue(Util.mapNumber(avbQty / qtyMonth * 30d));
            }
            if (qtyQuarterSplit != 0) {
                row.createCell(37).setCellValue(Util.mapNumber(qtySplit / qtyQuarterSplit * 90d));
                row.createCell(39).setCellValue(Util.mapNumber(avbQtySplit / qtyQuarterSplit * 90d));
            }
            if (qtyMonthSplit != 0) {
                row.createCell(38).setCellValue(Util.mapNumber(qtySplit / qtyMonthSplit * 30d));
                row.createCell(40).setCellValue(Util.mapNumber(avbQtySplit / qtyMonthSplit * 30d));
            }
            row.createCell(41).setCellValue(Util.stockStatus(qty, qtyMonth, 30, saleDate, now));
            row.createCell(42).setCellValue(Util.stockStatus(avbQty, qtyMonth, 30, saleDate, now));
            row.createCell(43).setCellValue(Util.stockStatus(qtySplit, qtyMonthSplit, 30, saleDate, now));
            row.createCell(44).setCellValue(Util.stockStatus(avbQtySplit, qtyMonthSplit, 30, saleDate, now));
            row.createCell(45).setCellValue(totalInstock);
            row.createCell(46).setCellValue(totalInstockSplit);
            row.createCell(47).setCellValue(totalSale);
            row.createCell(48).setCellValue(totalSaleSplit);
            row.createCell(49).setCellValue(resultSet.getInt("buy_times"));
            row.createCell(50).setCellValue(lastBuy);
            row.createCell(51).setCellValue(lastBuySplit);
            row.createCell(52).setCellValue(future);
            row.createCell(53).setCellValue(futureSplit);
            count5++;
        }
        ps.close();
    }
}
