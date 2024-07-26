package com.excel.imp;

import cn.hutool.core.date.DateTime;
import com.constant.StockConstant;
import com.excel.ExcelSheet;
import com.util.Util;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.*;

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
        CellStyle numberCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));
        numberCellStyle.setDataFormat(dataFormat.getFormat("#,##0"));

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
        rowField.createCell(24).setCellValue("近60天销量");
        rowField.createCell(25).setCellValue("近30天销量");
        rowField.createCell(26).setCellValue("第-4周销量");
        rowField.createCell(27).setCellValue("第-3周销量");
        rowField.createCell(28).setCellValue("第-2周销量");
        rowField.createCell(29).setCellValue("第-1周销量");
        rowField.createCell(30).setCellValue("近90天销量（拆中盒）");
        rowField.createCell(31).setCellValue("近60天销量（拆中盒）");
        rowField.createCell(32).setCellValue("近30天销量（拆中盒）");
        rowField.createCell(33).setCellValue("第-4周销量（拆中盒）");
        rowField.createCell(34).setCellValue("第-3周销量（拆中盒）");
        rowField.createCell(35).setCellValue("第-2周销量（拆中盒）");
        rowField.createCell(36).setCellValue("第-1周销量（拆中盒）");
        rowField.createCell(37).setCellValue("近14天平均销量");
        rowField.createCell(38).setCellValue("近14天平均销量（拆中盒）");
        rowField.createCell(39).setCellValue("近2周销售趋势");
        rowField.createCell(40).setCellValue("全部库存周转（近90天销量）");
        rowField.createCell(41).setCellValue("全部库存周转（近30天销量）");
        rowField.createCell(42).setCellValue("可用库存周转（近90天销量）");
        rowField.createCell(43).setCellValue("可用库存周转（近30天销量）");
        rowField.createCell(44).setCellValue("全部库存预警（近30天销量）");
        rowField.createCell(45).setCellValue("可用库存预警（近30天销量）");
        rowField.createCell(46).setCellValue("累计进货");
        rowField.createCell(47).setCellValue("累计进货（拆中盒）");
        rowField.createCell(48).setCellValue("近7天进货（拆中盒）");
        rowField.createCell(49).setCellValue("累计销售");
        rowField.createCell(50).setCellValue("累计销售（拆中盒）");
        rowField.createCell(51).setCellValue("采购次数");
        rowField.createCell(52).setCellValue("最后一次采购量");
        rowField.createCell(53).setCellValue("最后一次采购量（拆中盒）");
        rowField.createCell(54).setCellValue("最后一次采购时间");
        rowField.createCell(55).setCellValue("未到货");
        rowField.createCell(56).setCellValue("未到货（拆中盒）");
        rowField.createCell(57).setCellValue("是否包含[赠品]");
        rowField.createCell(58).setCellValue("近30天销售额");
        rowField.createCell(59).setCellValue("近60天销售额");
        rowField.createCell(60).setCellValue("近90天销售额");
        rowField.createCell(61).setCellValue("累计销售额");
        rowField.createCell(62).setCellValue("规格");

        Date now = DateTime.now().toSqlDate();

        int middleBoxFlag, modelPack, qty, avbQty, qtySplit, avbQtySplit, matchFlag;
        int qtyQuarter, qtyMonth, qty2Month, qtyWeek4, qtyWeek3, qtyWeek2, qtyWeek1, totalSale, totalInstock, lastBuy, future, instock7;
        int qtyQuarterSplit, qtyMonthSplit, qty2MonthSplit, qtyWeekSplit4, qtyWeekSplit3, qtyWeekSplit2, qtyWeekSplit1, totalSaleSplit, totalInstockSplit, lastBuySplit, futureSplit, instockSplit7;
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
            Date lastBuyTime = stockDetailResultSet.getDate("last_buy_time");

            qtyQuarter = stockDetailResultSet.getInt("qty_quarter");
            qtyMonth = stockDetailResultSet.getInt("qty_month");
            qty2Month = stockDetailResultSet.getInt("qty_2month");
            qtyWeek4 = stockDetailResultSet.getInt("qty_week4");
            qtyWeek3 = stockDetailResultSet.getInt("qty_week3");
            qtyWeek2 = stockDetailResultSet.getInt("qty_week2");
            qtyWeek1 = stockDetailResultSet.getInt("qty_week1");
            totalSale = stockDetailResultSet.getInt("total_sale");
            totalInstock = stockDetailResultSet.getInt("instock");
            instock7 = stockDetailResultSet.getInt("instock7");
            lastBuy = stockDetailResultSet.getInt("last_buy");
            future = stockDetailResultSet.getInt("future");
            matchFlag = stockDetailResultSet.getInt("match_flag");
            productSource = stockDetailResultSet.getString("product_source");

            qtyQuarterSplit = qtyQuarter;
            qtyMonthSplit = qtyMonth;
            qty2MonthSplit = qty2Month;
            qtyWeekSplit1 = qtyWeek1;
            qtyWeekSplit2 = qtyWeek2;
            qtyWeekSplit3 = qtyWeek3;
            qtyWeekSplit4 = qtyWeek4;
            totalSaleSplit = totalSale;
            totalInstockSplit = totalInstock;
            lastBuySplit = lastBuy;
            futureSplit = future;
            instockSplit7 = instock7;
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
                instockSplit7 *= modelPack;
                qty2MonthSplit *= modelPack;
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

            SXSSFCell cell17 = row.createCell(17);
            cell17.setCellStyle(numberCellStyle);
            cell17.setCellValue(qty);

            SXSSFCell cell18 = row.createCell(18);
            cell18.setCellStyle(numberCellStyle);
            cell18.setCellValue(avbQty);

            SXSSFCell cell19 = row.createCell(19);
            cell19.setCellStyle(numberCellStyle);
            cell19.setCellValue(qtySplit);

            SXSSFCell cell20 = row.createCell(20);
            cell20.setCellStyle(numberCellStyle);
            cell20.setCellValue(avbQtySplit);

            SXSSFCell cell21 = row.createCell(21);
            cell21.setCellStyle(numberCellStyle);
            cell21.setCellValue(qty - avbQty);

            SXSSFCell cell22 = row.createCell(22);
            cell22.setCellStyle(numberCellStyle);
            cell22.setCellValue(qtySplit - avbQtySplit);

            SXSSFCell cell23 = row.createCell(23);
            cell23.setCellStyle(numberCellStyle);
            cell23.setCellValue(qtyQuarter);

            SXSSFCell cell24 = row.createCell(24);
            cell24.setCellStyle(numberCellStyle);
            cell24.setCellValue(qty2Month);

            SXSSFCell cell25 = row.createCell(25);
            cell25.setCellStyle(numberCellStyle);
            cell25.setCellValue(qtyMonth);

            SXSSFCell cell26 = row.createCell(26);
            cell26.setCellStyle(numberCellStyle);
            cell26.setCellValue(qtyWeek4);

            SXSSFCell cell27 = row.createCell(27);
            cell27.setCellStyle(numberCellStyle);
            cell27.setCellValue(qtyWeek3);

            SXSSFCell cell28 = row.createCell(28);
            cell28.setCellStyle(numberCellStyle);
            cell28.setCellValue(qtyWeek2);

            SXSSFCell cell29 = row.createCell(29);
            cell29.setCellStyle(numberCellStyle);
            cell29.setCellValue(qtyWeek1);

            SXSSFCell cell30 = row.createCell(30);
            cell30.setCellStyle(numberCellStyle);
            cell30.setCellValue(qtyQuarterSplit);

            SXSSFCell cell31 = row.createCell(31);
            cell31.setCellStyle(numberCellStyle);
            cell31.setCellValue(qty2MonthSplit);

            SXSSFCell cell32 = row.createCell(32);
            cell32.setCellStyle(numberCellStyle);
            cell32.setCellValue(qtyMonthSplit);

            SXSSFCell cell33 = row.createCell(33);
            cell33.setCellStyle(numberCellStyle);
            cell33.setCellValue(qtyWeekSplit4);

            SXSSFCell cell34 = row.createCell(34);
            cell34.setCellStyle(numberCellStyle);
            cell34.setCellValue(qtyWeekSplit3);

            SXSSFCell cell35 = row.createCell(35);
            cell35.setCellStyle(numberCellStyle);
            cell35.setCellValue(qtyWeekSplit2);

            SXSSFCell cell36 = row.createCell(36);
            cell36.setCellStyle(numberCellStyle);
            cell36.setCellValue(qtyWeekSplit1);

            row.createCell(37).setCellValue(Util.mapNumber(((double) qtyWeek1 + (double) qtyWeek2) / 14d));
            row.createCell(38).setCellValue(Util.mapNumber(((double) qtyWeekSplit1 + (double) qtyWeekSplit2) / 14d));
            row.createCell(39).setCellValue(Util.trend(qtyWeek1, qtyWeek2));
            if (qtyQuarter != 0) {
                row.createCell(40).setCellValue(Util.mapNumber(((double) qty) / ((double) qtyQuarter) * 90d));
                row.createCell(42).setCellValue(Util.mapNumber(((double) avbQty) / ((double) qtyQuarter) * 90d));
            }
            if (qtyMonth != 0) {
                row.createCell(41).setCellValue(Util.mapNumber(((double) qty) / ((double) qtyMonth) * 30d));
                row.createCell(43).setCellValue(Util.mapNumber(((double) avbQty) / ((double) qtyMonth) * 30d));
            }
            row.createCell(44).setCellValue(Util.stockStatus(qty, qtyMonth, 30, saleDate, now));
            row.createCell(45).setCellValue(Util.stockStatus(avbQty, qtyMonth, 30, saleDate, now));

            SXSSFCell cellTotalInstock = row.createCell(46);
            cellTotalInstock.setCellStyle(numberCellStyle);
            cellTotalInstock.setCellValue(totalInstock);
            SXSSFCell cellTotalInstockSplit = row.createCell(47);
            cellTotalInstockSplit.setCellStyle(numberCellStyle);
            cellTotalInstockSplit.setCellValue(totalInstockSplit);

            SXSSFCell cellInstockSplit7 = row.createCell(48);
            cellInstockSplit7.setCellStyle(numberCellStyle);
            cellInstockSplit7.setCellValue(instockSplit7);

            SXSSFCell cellTotalSale = row.createCell(49);
            cellTotalSale.setCellStyle(numberCellStyle);
            cellTotalSale.setCellValue(totalSale);
            SXSSFCell cellTotalSaleSplit = row.createCell(50);
            cellTotalSaleSplit.setCellStyle(numberCellStyle);
            cellTotalSaleSplit.setCellValue(totalSaleSplit);

            SXSSFCell cellBuyTimes = row.createCell(51);
            cellBuyTimes.setCellStyle(numberCellStyle);
            cellBuyTimes.setCellValue(stockDetailResultSet.getInt("buy_times"));

            SXSSFCell cellLastBuy = row.createCell(52);
            cellLastBuy.setCellStyle(numberCellStyle);
            cellLastBuy.setCellValue(lastBuy);
            SXSSFCell cellLastBuySplit = row.createCell(53);
            cellLastBuySplit.setCellStyle(numberCellStyle);
            cellLastBuySplit.setCellValue(lastBuySplit);

            if (lastBuyTime != null) {
                SXSSFCell cell = row.createCell(54);
                cell.setCellStyle(dateCellStyle);
                cell.setCellValue(lastBuyTime);
            }

            SXSSFCell cellFuture = row.createCell(55);
            cellFuture.setCellStyle(numberCellStyle);
            cellFuture.setCellValue(future);
            SXSSFCell cellFutureSplit = row.createCell(56);
            cellFutureSplit.setCellStyle(numberCellStyle);
            cellFutureSplit.setCellValue(futureSplit);

            row.createCell(57).setCellValue(materialName.contains("赠品"));

            SXSSFCell cell58 = row.createCell(58);
            cell58.setCellStyle(numberCellStyle);
            cell58.setCellValue(stockDetailResultSet.getDouble("sale30"));
            SXSSFCell cell59 = row.createCell(59);
            cell59.setCellStyle(numberCellStyle);
            cell59.setCellValue(stockDetailResultSet.getDouble("sale60"));
            SXSSFCell cell60 = row.createCell(60);
            cell60.setCellStyle(numberCellStyle);
            cell60.setCellValue(stockDetailResultSet.getDouble("sale90"));
            SXSSFCell cell61 = row.createCell(61);
            cell61.setCellStyle(numberCellStyle);
            cell61.setCellValue(stockDetailResultSet.getDouble("sale_amount"));

            row.createCell(62).setCellValue(stockDetailResultSet.getString("specification"));

            count++;
        }
        CellRangeAddress range = new CellRangeAddress(1, sheet.getLastRowNum(), 0, 62);
        sheet.setAutoFilter(range);
        ps.close();
    }
}
