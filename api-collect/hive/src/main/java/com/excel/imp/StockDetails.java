package com.excel.imp;

import cn.hutool.core.date.DateTime;
import com.constant.StockConstant;
import com.excel.ExcelSheet;
import com.util.Util;
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

public class StockDetails implements ExcelSheet {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public StockDetails(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));

        // 物料 - 详情
        SXSSFSheet sheet = xssfWorkbook.createSheet(table);
        PreparedStatement ps = hiveConnection.prepareStatement(sql);
        ResultSet stockDetailResultSet = ps.executeQuery();
        sheet.createRow(0).createCell(0).setCellValue(table);

        SXSSFRow rowField = sheet.createRow(1);
        rowField.createCell(0).setCellValue("物料编码");
        rowField.createCell(1).setCellValue("物料名称");
        rowField.createCell(2).setCellValue("产品来源");
        rowField.createCell(3).setCellValue("商品类型");
        rowField.createCell(4).setCellValue("产品线");
        rowField.createCell(5).setCellValue("产品系列");
        rowField.createCell(6).setCellValue("IP细分");
        rowField.createCell(7).setCellValue("上市日期");
        rowField.createCell(8).setCellValue("货龄");
        rowField.createCell(9).setCellValue("销售价");
        rowField.createCell(10).setCellValue("成本");
        rowField.createCell(11).setCellValue("成本（含税）");
        rowField.createCell(12).setCellValue("盒规");
        rowField.createCell(13).setCellValue("仓库代码");
        rowField.createCell(14).setCellValue("仓库名称");
        rowField.createCell(15).setCellValue("渠道");
        rowField.createCell(16).setCellValue("仓库分组");
        rowField.createCell(17).setCellValue("全部库存");
        rowField.createCell(18).setCellValue("可用库存");
        rowField.createCell(19).setCellValue("全部库存（拆中盒）");
        rowField.createCell(20).setCellValue("可用库存（拆中盒）");
        rowField.createCell(21).setCellValue("占用库存");
        rowField.createCell(22).setCellValue("占用库存（拆中盒）");
        rowField.createCell(23).setCellValue("近90天销量");
        rowField.createCell(24).setCellValue("近30天销量");
        rowField.createCell(25).setCellValue("第-4周销量");
        rowField.createCell(26).setCellValue("第-3周销量");
        rowField.createCell(27).setCellValue("第-2周销量");
        rowField.createCell(28).setCellValue("第-1周销量");
        rowField.createCell(29).setCellValue("近90天销量（拆中盒）");
        rowField.createCell(30).setCellValue("近30天销量（拆中盒）");
        rowField.createCell(31).setCellValue("第-4周销量（拆中盒）");
        rowField.createCell(32).setCellValue("第-3周销量（拆中盒）");
        rowField.createCell(33).setCellValue("第-2周销量（拆中盒）");
        rowField.createCell(34).setCellValue("第-1周销量（拆中盒）");
        rowField.createCell(35).setCellValue("近14天平均销量");
        rowField.createCell(36).setCellValue("近14天平均销量（拆中盒）");
        rowField.createCell(37).setCellValue("近2周销售趋势");
        rowField.createCell(38).setCellValue("全部库存周转（近90天销量）");
        rowField.createCell(39).setCellValue("全部库存周转（近30天销量）");
        rowField.createCell(40).setCellValue("可用库存周转（近90天销量）");
        rowField.createCell(41).setCellValue("可用库存周转（近30天销量）");
        rowField.createCell(42).setCellValue("全部库存预警（近30天销量）");
        rowField.createCell(43).setCellValue("可用库存预警（近30天销量）");
        rowField.createCell(44).setCellValue("累计进货");
        rowField.createCell(45).setCellValue("累计进货（拆中盒）");
        rowField.createCell(46).setCellValue("累计销售");
        rowField.createCell(47).setCellValue("累计销售（拆中盒）");
        rowField.createCell(48).setCellValue("采购次数");
        rowField.createCell(49).setCellValue("最后一次采购量");
        rowField.createCell(50).setCellValue("最后一次采购量（拆中盒）");
        rowField.createCell(51).setCellValue("未到货");
        rowField.createCell(52).setCellValue("未到货（拆中盒）");
        rowField.createCell(53).setCellValue("是否包含[赠品]");

        Date now = DateTime.now().toSqlDate();

        int middleBoxFlag, modelPack, qty, avbQty, qtySplit, avbQtySplit, matchFlag;
        int qtyQuarter, qtyMonth, qtyWeek4, qtyWeek3, qtyWeek2, qtyWeek1, totalSale, totalInstock, lastBuy, future;
        int qtyQuarterSplit, qtyMonthSplit, qtyWeekSplit4, qtyWeekSplit3, qtyWeekSplit2, qtyWeekSplit1, totalSaleSplit, totalInstockSplit, lastBuySplit, futureSplit;
        double cost, costWithTax;
        String productSource, materialName;

        int count = 2;
        while (stockDetailResultSet.next()) {
            SXSSFRow row = sheet.createRow(count);
            middleBoxFlag = stockDetailResultSet.getInt("middle_box_flag");
            modelPack = stockDetailResultSet.getInt("pack_model");
            qty = stockDetailResultSet.getInt("qty");
            avbQty = stockDetailResultSet.getInt("avb_qty");
            qtySplit = qty;
            avbQtySplit = avbQty;
            Date saleDate = stockDetailResultSet.getDate("sale_date");
            qtyQuarter = stockDetailResultSet.getInt("qty_quarter");
            qtyMonth = stockDetailResultSet.getInt("qty_month");
            qtyWeek4 = stockDetailResultSet.getInt("qty_week4");
            qtyWeek3 = stockDetailResultSet.getInt("qty_week3");
            qtyWeek2 = stockDetailResultSet.getInt("qty_week2");
            qtyWeek1 = stockDetailResultSet.getInt("qty_week1");
            totalSale = stockDetailResultSet.getInt("total_sale");
            totalInstock = stockDetailResultSet.getInt("instock");
            lastBuy = stockDetailResultSet.getInt("last_buy");
            future = stockDetailResultSet.getInt("future");
            matchFlag = stockDetailResultSet.getInt("match_flag");
            productSource = stockDetailResultSet.getString("product_source");

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
            cost = stockDetailResultSet.getDouble("cost");
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
            materialName = stockDetailResultSet.getString("material_name");
            row.createCell(0).setCellValue(stockDetailResultSet.getString("material_number"));
            row.createCell(1).setCellValue(materialName);
            row.createCell(2).setCellValue(stockDetailResultSet.getString("product_source"));
            row.createCell(3).setCellValue(stockDetailResultSet.getString("product_type"));
            row.createCell(4).setCellValue(stockDetailResultSet.getString("product_line"));
            row.createCell(5).setCellValue(stockDetailResultSet.getString("product_series"));
            row.createCell(6).setCellValue(stockDetailResultSet.getString("ip_sub"));
            if (saleDate != null
                    && saleDate.before(StockConstant.MAX_DATE)
                    && productSource != null
                    && !(saleDate.after(StockConstant.HIDE_DATE) && matchFlag != 1 && productSource.equals("自主研发"))) {
                SXSSFCell cell7 = row.createCell(7);
                cell7.setCellStyle(dateCellStyle);
                cell7.setCellValue(saleDate);
            }
            row.createCell(8).setCellValue(Util.mapStockAge(saleDate));
            row.createCell(9).setCellValue(stockDetailResultSet.getDouble("sale_price"));
            row.createCell(10).setCellValue(cost);
            row.createCell(11).setCellValue(costWithTax);
            row.createCell(12).setCellValue(stockDetailResultSet.getInt("pack_model"));
            row.createCell(13).setCellValue(stockDetailResultSet.getString("stock_number"));
            row.createCell(14).setCellValue(stockDetailResultSet.getString("stock_name"));
            row.createCell(15).setCellValue(stockDetailResultSet.getString("channel"));
            row.createCell(16).setCellValue(stockDetailResultSet.getString("fgroup_name"));
            row.createCell(17).setCellValue(qty);
            row.createCell(18).setCellValue(avbQty);
            row.createCell(19).setCellValue(qtySplit);
            row.createCell(20).setCellValue(avbQtySplit);
            row.createCell(21).setCellValue(qty - avbQty);
            row.createCell(22).setCellValue(qtySplit - avbQtySplit);
            row.createCell(23).setCellValue(qtyQuarter);
            row.createCell(24).setCellValue(qtyMonth);
            row.createCell(25).setCellValue(qtyWeek4);
            row.createCell(26).setCellValue(qtyWeek3);
            row.createCell(27).setCellValue(qtyWeek2);
            row.createCell(28).setCellValue(qtyWeek1);
            row.createCell(29).setCellValue(qtyQuarterSplit);
            row.createCell(30).setCellValue(qtyMonthSplit);
            row.createCell(31).setCellValue(qtyWeekSplit4);
            row.createCell(32).setCellValue(qtyWeekSplit3);
            row.createCell(33).setCellValue(qtyWeekSplit2);
            row.createCell(34).setCellValue(qtyWeekSplit1);
            row.createCell(35).setCellValue(Util.mapNumber(((double) qtyWeek1 + (double) qtyWeek2) / 14d));
            row.createCell(36).setCellValue(Util.mapNumber(((double) qtyWeekSplit1 + (double) qtyWeekSplit2) / 14d));
            row.createCell(37).setCellValue(Util.trend(qtyWeek1, qtyWeek2));
            if (qtyQuarter != 0) {
                row.createCell(38).setCellValue(Util.mapNumber(((double) qty) / ((double) qtyQuarter) * 90d));
                row.createCell(40).setCellValue(Util.mapNumber(((double) avbQty) / ((double) qtyQuarter) * 90d));
            }
            if (qtyMonth != 0) {
                row.createCell(39).setCellValue(Util.mapNumber(((double) qty) / ((double) qtyMonth) * 30d));
                row.createCell(41).setCellValue(Util.mapNumber(((double) avbQty) / ((double) qtyMonth) * 30d));
            }
            row.createCell(42).setCellValue(Util.stockStatus(qty, qtyMonth, 30, saleDate, now));
            row.createCell(43).setCellValue(Util.stockStatus(avbQty, qtyMonth, 30, saleDate, now));
            row.createCell(44).setCellValue(totalInstock);
            row.createCell(45).setCellValue(totalInstockSplit);
            row.createCell(46).setCellValue(totalSale);
            row.createCell(47).setCellValue(totalSaleSplit);
            row.createCell(48).setCellValue(stockDetailResultSet.getInt("buy_times"));
            row.createCell(49).setCellValue(lastBuy);
            row.createCell(50).setCellValue(lastBuySplit);
            row.createCell(51).setCellValue(future);
            row.createCell(52).setCellValue(futureSplit);
            row.createCell(53).setCellValue(materialName.contains("赠品"));
            count++;
        }
        ps.close();
    }
}
