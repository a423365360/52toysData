package com.excel.imp;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
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

public class StockTuTu implements ExcelSheet {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public StockTuTu(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));
        Date today = DateTime.now().toSqlDate();

        CellStyle percentStyle = xssfWorkbook.createCellStyle();
        percentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));

        // 使用 DataFormat 对象创建一个数字格式
        CellStyle periodStyle = xssfWorkbook.createCellStyle();
        periodStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));

        // 物料 - 渠道
        SXSSFSheet sheet5 = xssfWorkbook.createSheet(table);
        PreparedStatement ps = hiveConnection.prepareStatement(sql);
        ResultSet resultSet5 = ps.executeQuery();
        SXSSFRow rowField5 = sheet5.createRow(0);
        rowField5.createCell(0).setCellValue("产品系列");
        rowField5.createCell(1).setCellValue("产品线");
        rowField5.createCell(2).setCellValue("产品来源");
        rowField5.createCell(3).setCellValue("发售日期");
        rowField5.createCell(4).setCellValue("货龄");
        String title = "";
        for (int i = 0; i < 7; i++) {
            int j = i * 22;
            switch (i) {
                case 0:
                    title = "批发-";
                    break;
                case 1:
                    title = "直营门店-";
                    break;
                case 2:
                    title = "电商-";
                    break;
                case 3:
                    title = "直播-";
                    break;
                case 4:
                    title = "官方商城-";
                    break;
                case 5:
                    title = "海外渠道-";
                    break;
                case 6:
                    title = "海外电商-";
                    break;
            }
            rowField5.createCell(5 + j).setCellValue(title + "库存");
            rowField5.createCell(6 + j).setCellValue(title + "可用库存");
            rowField5.createCell(7 + j).setCellValue(title + "库存(拆中盒)");
            rowField5.createCell(8 + j).setCellValue(title + "可用库存(拆中盒)");
            rowField5.createCell(9 + j).setCellValue(title + "近三月销量");
            rowField5.createCell(10 + j).setCellValue(title + "近一月销量");
            rowField5.createCell(11 + j).setCellValue(title + "近一周销量");
            rowField5.createCell(12 + j).setCellValue(title + "近三月销量(拆中盒)");
            rowField5.createCell(13 + j).setCellValue(title + "近一月销量(拆中盒)");
            rowField5.createCell(14 + j).setCellValue(title + "近三月销量(拆中盒)");
            rowField5.createCell(15 + j).setCellValue(title + "全部库存近3月销售周转");
            rowField5.createCell(16 + j).setCellValue(title + "全部库存近1月销售周转");
            rowField5.createCell(17 + j).setCellValue(title + "全部库存近1周销售周转");
            rowField5.createCell(18 + j).setCellValue(title + "有效库存近3月销售周转");
            rowField5.createCell(19 + j).setCellValue(title + "有效库存近1月销售周转");
            rowField5.createCell(20 + j).setCellValue(title + "有效库存近1周销售周转");
            rowField5.createCell(21 + j).setCellValue(title + "全部库存近3月销售周转(拆中盒)");
            rowField5.createCell(22 + j).setCellValue(title + "全部库存近1月销售周转(拆中盒)");
            rowField5.createCell(23 + j).setCellValue(title + "全部库存近1周销售周转(拆中盒)");
            rowField5.createCell(24 + j).setCellValue(title + "有效库存近3月销售周转(拆中盒)");
            rowField5.createCell(25 + j).setCellValue(title + "有效库存近1月销售周转(拆中盒)");
            rowField5.createCell(26 + j).setCellValue(title + "有效库存近1周销售周转(拆中盒)");
        }

        int count5 = 0;
        String productSeriesFlag = "";
        String productSeries, channel, stockAge = "";
        SXSSFRow row5 = null;
        int range, rangeIndex = 0;
        while (resultSet5.next()) {
            productSeries = resultSet5.getString("product_series");
            if (!productSeries.equals(productSeriesFlag)) {
                count5++;
                productSeriesFlag = productSeries;
                row5 = sheet5.createRow(count5);
                row5.createCell(0).setCellValue(productSeries);
                row5.createCell(1).setCellValue(resultSet5.getString("product_line"));
                row5.createCell(2).setCellValue(resultSet5.getString("product_source"));
                SXSSFCell cell52 = row5.createCell(3);
                cell52.setCellStyle(dateCellStyle);
                Date saleDate = resultSet5.getDate("sale_date");
                if (saleDate != null) {
                    long between = DateUtil.between(saleDate, today, DateUnit.DAY, false);
                    if (between <= 90) {
                        stockAge = "1-3个月";
                    } else if (between > 90 && between <= 180) {
                        stockAge = "4-6个月";
                    } else if (between > 180 && between <= 365) {
                        stockAge = "7-12个月";
                    } else if (between > 365 && between <= 730) {
                        stockAge = "1-2年";
                    } else if (between > 730 && between <= 1095) {
                        stockAge = "2-3年";
                    } else if (between > 1095) {
                        stockAge = "3年以上";
                    }
                    cell52.setCellValue(saleDate);
                    row5.createCell(4).setCellValue(stockAge);
                }
            }

            // 渠道
            channel = resultSet5.getString("channel");
            switch (channel) {
                case "批发":
                    rangeIndex = 0;
                    break;
                case "直营门店":
                    rangeIndex = 1;
                    break;
                case "电商":
                    rangeIndex = 2;
                    break;
                case "直播":
                    rangeIndex = 3;
                    break;
                case "官方商城":
                    rangeIndex = 4;
                    break;
                case "海外渠道":
                    rangeIndex = 5;
                    break;
                case "海外电商":
                    rangeIndex = 6;
                    break;
            }
            range = 22 * rangeIndex;

            double qty = resultSet5.getDouble("qty");
            row5.createCell(5 + range).setCellValue(qty);
            double avb_qty = resultSet5.getDouble("avb_qty");
            row5.createCell(6 + range).setCellValue(avb_qty);
            double qty_split = resultSet5.getDouble("qty_split");
            row5.createCell(7 + range).setCellValue(qty_split);
            double avb_qty_split = resultSet5.getDouble("avb_qty_split");
            row5.createCell(8 + range).setCellValue(avb_qty_split);
            double qty_quarter = resultSet5.getDouble("qty_quarter");
            row5.createCell(9 + range).setCellValue(qty_quarter);
            double qty_month = resultSet5.getDouble("qty_month");
            row5.createCell(10 + range).setCellValue(qty_month);
            double qty_week1 = resultSet5.getDouble("qty_week1");
            row5.createCell(11 + range).setCellValue(qty_week1);
            double qty_quarter_split = resultSet5.getDouble("qty_quarter_split");
            row5.createCell(12 + range).setCellValue(qty_quarter_split);
            double qty_month_split = resultSet5.getDouble("qty_month_split");
            row5.createCell(13 + range).setCellValue(qty_month_split);
            double qty_week_split1 = resultSet5.getDouble("qty_week_split1");
            row5.createCell(14 + range).setCellValue(qty_week_split1);

            if (qty_quarter != 0) {
                row5.createCell(15 + range).setCellValue(Util.mapNumber(qty / qty_quarter * 90d));
                row5.createCell(18 + range).setCellValue(Util.mapNumber(avb_qty / qty_quarter * 90d));
            }
            if (qty_month != 0) {
                row5.createCell(16 + range).setCellValue(Util.mapNumber(qty / qty_month * 30d));
                row5.createCell(19 + range).setCellValue(Util.mapNumber(avb_qty / qty_month * 30d));
            }
            if (qty_week1 != 0) {
                row5.createCell(17 + range).setCellValue(Util.mapNumber(qty / qty_week1 * 7d));
                row5.createCell(20 + range).setCellValue(Util.mapNumber(avb_qty / qty_week1 * 7d));
            }

            if (qty_quarter_split != 0) {
                row5.createCell(21 + range).setCellValue(Util.mapNumber(qty_split / qty_quarter_split * 90d));
                row5.createCell(24 + range).setCellValue(Util.mapNumber(avb_qty_split / qty_quarter_split * 90d));
            }
            if (qty_month_split != 0) {
                row5.createCell(22 + range).setCellValue(Util.mapNumber(qty_split / qty_month_split * 30d));
                row5.createCell(25 + range).setCellValue(Util.mapNumber(avb_qty_split / qty_month_split * 30d));
            }
            if (qty_week_split1 != 0) {
                row5.createCell(23 + range).setCellValue(Util.mapNumber(qty_split / qty_week_split1 * 7d));
                row5.createCell(26 + range).setCellValue(Util.mapNumber(avb_qty_split / qty_week_split1 * 7d));
            }
        }
        ps.close();
    }
}
