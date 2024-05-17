package com.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.bean.ReportBean;
import com.constant.ReportType;
import com.excel.imp.*;
import com.excel.imp2.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataUtil {
    public static ReportBean getDayReport(Connection hiveConnection, String endDayInput, String testFlag, int sign) throws Exception {
        SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(1000);

        String endDay = endDayInput;
        // 查询分区
        DateTime yesterday = DateTime.of(endDay, "yyyy-MM-dd").offset(DateField.HOUR, -24 * 1);
        String dt = yesterday.toDateStr();
        String year = yesterday.toString("yyyy");
        String month = yesterday.toString("yyyy-MM");
        int quarter = yesterday.quarter();

        String path = "/datadisk/javalog/dws/DayReport_" + endDay + "_" + sign + DateTime.now().getTime() + ".xlsx";
        if ("0".equals(testFlag)) {
            path = "D:\\test\\DayReport_" + endDay + "_" + sign + DateTime.now().getTime() + ".xlsx";
        }

        // 业务线达成-月
        String businessLineInReachSql = "SELECT * FROM "
                + "ads_business_line_reach WHERE dt = '" + dt + "' AND id < 100 ORDER BY id";
        String businessLineInReachTotalSql = "SELECT * FROM "
                + "ads_business_line_reach_total WHERE dt = '" + dt + "'";
        AdsBusinessReach adsBusinessReach = new AdsBusinessReach(xssfWorkbook, hiveConnection);
        try {
            adsBusinessReach.setSheet("业务线达成", businessLineInReachSql, businessLineInReachTotalSql, dt);
        } catch (Exception e) {
        }

        // 业务线达成-月-清货
        String businessLineInReachMonthSql = "SELECT * FROM "
                + "ads_business_line_reach_month WHERE dt = '" + month + "' AND id < 100";
        String businessLineInReachTotalMonthSql = "SELECT * FROM "
                + "ads_business_line_reach_total_month WHERE dt = '" + month + "'";
        AdsBusinessReach adsBusinessReachMonth = new AdsBusinessReach(xssfWorkbook, hiveConnection);
        try {
            adsBusinessReachMonth.setSheet("业务线达成-清货", businessLineInReachMonthSql, businessLineInReachTotalMonthSql, dt);
        } catch (Exception e) {
        }

        // 业务线达成-季度
        String quarterPartition = year + "-" + quarter;
        String businessLineInReachQuarterSql = "SELECT * FROM "
                + "ads_business_line_reach_quarter WHERE dt = '" + quarterPartition + "' AND id < 100";
        String businessLineInReachTotalQuarterSql = "SELECT * FROM "
                + "ads_business_line_reach_total_quarter WHERE dt = '" + quarterPartition + "'";
        AdsBusinessReach adsBusinessReachSession = new AdsBusinessReach(xssfWorkbook, hiveConnection);
        try {
            adsBusinessReachSession.setSheet("业务线达成-季度", businessLineInReachQuarterSql, businessLineInReachTotalQuarterSql, dt);
        } catch (Exception e) {
        }

        // 业务线达成-年
        String businessLineInReachYearSql = "SELECT * FROM "
                + "ads_business_line_reach_year WHERE dt = '" + year + "' AND id < 100";
        AdsBusinessReachYear adsBusinessReachYear = new AdsBusinessReachYear(xssfWorkbook, hiveConnection);
        try {
            adsBusinessReachYear.setSheet("业务线达成-年(测试)", businessLineInReachYearSql, dt);
        } catch (Exception e) {
        }

        // 日销售数据
        String dayInWeekSql = "SELECT * FROM ads_day_in_week WHERE dt = '" + dt + "' AND id < 100 ORDER BY id";
        String dayInWeekTotalSql = "SELECT * FROM ads_day_in_week_total WHERE dt = '" + dt + "'";
        String dayInWeekExtraSql = "SELECT * FROM ads_day_in_week_extra WHERE dt = '" + dt + "'";
        AdsDayInWeek adsDayInWeek = new AdsDayInWeek(xssfWorkbook, hiveConnection);
        try {
            adsDayInWeek.setSheet("本周日销售额", dayInWeekSql, dayInWeekTotalSql, dayInWeekExtraSql, yesterday);
        } catch (Exception e) {
        }

        // 当月日销售量
        String dayInMonth = "SELECT * FROM ads_day_in_month WHERE dt='" + dt + "'";
        String dayInMonthTotal = "SELECT * FROM ads_day_in_month_total WHERE dt='" + dt + "'";
        String dayInMonthExtra = "SELECT * FROM ads_day_in_month_extra WHERE dt='" + dt + "'";
        AdsDayInMonth adsDayInMonth = new AdsDayInMonth(xssfWorkbook, hiveConnection);
        try {
            adsDayInMonth.setSheet("本月日销售额", dayInMonth, dayInMonthTotal, dayInMonthExtra, yesterday);
        } catch (Exception e) {
        }

        // 主要系列
        String topBusinessLineSql = "SELECT * FROM top_product_series WHERE dt = '" + dt + "' ORDER BY id";
        // 主要客户
        String mainCustomerPivotSql = "SELECT * FROM "
                + "ads_main_customer_pivot WHERE dt = '" + dt + "' ORDER BY customer_rank";
        AdsMainCustomer adsMainCustomer = new AdsMainCustomer(xssfWorkbook, hiveConnection);
        try {
            adsMainCustomer.setSheet("主要客户", topBusinessLineSql, mainCustomerPivotSql, dt);
        } catch (Exception e) {
        }

        // 自研产品
        String vsSelf14 = "SELECT * FROM "
                + "ads_self_development_14 WHERE dt='" + dt + "' ORDER BY product_line DESC, business_line, sale_time DESC";
        AdsSelf adsSelf = new AdsSelf(xssfWorkbook, hiveConnection);
        try {
            adsSelf.setSheet("自研产品", vsSelf14, vsSelf14, dt);
        } catch (Exception e) {
        }

        // 批发客户
        String vsTopCustomerSql = "SELECT * FROM "
                + "ads_vs_top_customer WHERE dt='" + dt + "' ORDER BY rk1, rank0";
        String vsTotalSql = "SELECT rk1, total_result2 FROM ads_vs_total_sale WHERE dt='" + dt + "'";
        AdsVsTopCustomer adsVsTopCustomer = new AdsVsTopCustomer(xssfWorkbook, hiveConnection);
        try {
            adsVsTopCustomer.setSheet("批发客户", vsTopCustomerSql, vsTotalSql, dt);
        } catch (Exception e) {
        }

        // 新品对标产品
        String vsProductSeriesSql = "SELECT * FROM "
                + "ads_vs_14 WHERE dt='" + dt + "' ORDER BY rk3, rk1, rk2, id";
        String vsProductSeriesFirstDaySql = "SELECT product_series, total_number FROM ads_vs_first_day WHERE dt ='" + dt + "'";
        AdsVs14 adsVs14 = new AdsVs14(xssfWorkbook, hiveConnection);
        try {
            adsVs14.setSheet("新品对标产品", vsProductSeriesSql, vsProductSeriesFirstDaySql, dt);
        } catch (Exception e) {
        }

        // 新品对标产品(物料)
        String vsMaterialSql = "SELECT * FROM "
                + "ads_new_14 WHERE dt='" + dt + "' AND material_name IS NOT NULL ORDER BY rk1, rk2, material_name, id";
        String vsMaterialFirstDaySql = "SELECT material_name, total_number FROM ads_new_first_day WHERE dt ='" + dt + "'";
        AdsVsMaterial14 adsVsMaterial14 = new AdsVsMaterial14(xssfWorkbook, hiveConnection);
        try {
            adsVsMaterial14.setSheet("新品对标产品(物料)", vsMaterialSql, vsMaterialFirstDaySql, dt);
        } catch (Exception e) {
        }

        // 油菜花
        String youcaihuaSql1 = "SELECT * FROM ads_youcaihua_panel WHERE dt='" + dt + "'";
        String youcaihuaSql2 = "SELECT * FROM ads_youcaihua_panel_field WHERE dt='" + dt + "'";
        AdsYoucaihua youcaihua = new AdsYoucaihua(xssfWorkbook, hiveConnection);
        try {
            youcaihua.setSheet("油菜花-娃娃机", youcaihuaSql1, youcaihuaSql2, yesterday);
        } catch (Exception e) {
        }

        FileOutputStream fileOutputStream = new FileOutputStream(path);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        xssfWorkbook.write(bufferedOutputStream);
        bufferedOutputStream.close();
        fileOutputStream.close();
        xssfWorkbook.close();
        return new ReportBean(path, sign, dt);
    }

    public static ReportBean getFilePath(Connection hiveConnection, String endDayInput, String testFlag, int sign) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        SXSSFSheet sheet = workbook.createSheet("金蝶数据");
        CellStyle dateCellStyle = workbook.createCellStyle();
        CellStyle timestampCellStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));
        timestampCellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd HH:mm:ss"));

        String endDay = endDayInput;
        // 查询分区
        String dt = DateTime.of(endDay, "yyyy-MM-dd").offset(DateField.HOUR, -24 * 1).toDateStr();
        // 查询数据
        String startDay = DateTime.of(endDay, "yyyy-MM-dd").offset(DateField.HOUR, -24 * 1).toDateStr();
        if (sign == ReportType.WEEK) {
            startDay = DateTime.of(endDay, "yyyy-MM-dd").offset(DateField.HOUR, -24 * 7).toDateStr();
        }
        if (sign == ReportType.MONTH) {
            startDay = DateTime.of(endDay, "yyyy-MM-dd").offset(DateField.MONTH, -1).toDateStr().substring(0, 7) + "-01";
            endDay = endDay.substring(0, 7) + "-01";
        }

        String querySQL = "SELECT * FROM "
                + "dws_sal_stock where dt='" + dt
                + "' AND fdate<'" + endDay + "'"
                + " AND fdate>='" + startDay + "'";
        PreparedStatement preparedStatement = hiveConnection.prepareStatement(querySQL);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.setFetchSize(5000);

        SXSSFRow rowField = sheet.createRow(0);
        rowField.createCell(0).setCellValue("单据编号");
        rowField.createCell(1).setCellValue("日期");
        rowField.createCell(2).setCellValue("物料编码");
        rowField.createCell(3).setCellValue("物料名称");
        rowField.createCell(4).setCellValue("实发数量");
        rowField.createCell(5).setCellValue("盒规");
        rowField.createCell(6).setCellValue("实发数量*盒规");
        rowField.createCell(7).setCellValue("含税单价");
        rowField.createCell(8).setCellValue("价税合计（本位币）");
        rowField.createCell(9).setCellValue("客户");
        rowField.createCell(10).setCellValue("客户分组");
        rowField.createCell(11).setCellValue("销售部门名称");
        rowField.createCell(12).setCellValue("仓库");
        rowField.createCell(13).setCellValue("业务线");
        rowField.createCell(14).setCellValue("线上线下");
        rowField.createCell(15).setCellValue("产品来源");
        rowField.createCell(16).setCellValue("产品系列");
        rowField.createCell(17).setCellValue("产品线");
        rowField.createCell(18).setCellValue("IP归属");
        rowField.createCell(19).setCellValue("IP细分");
        rowField.createCell(20).setCellValue("商品类型");
        rowField.createCell(21).setCellValue("上市日期");
        rowField.createCell(22).setCellValue("销售周期(天)");
        rowField.createCell(23).setCellValue("是否新品");
        rowField.createCell(24).setCellValue("是否统计");
        rowField.createCell(25).setCellValue("业务线判断来源");
        rowField.createCell(26).setCellValue("数据类型");
        rowField.createCell(27).setCellValue("结算组织");
        rowField.createCell(28).setCellValue("售价");
        rowField.createCell(29).setCellValue("销售折扣");
        rowField.createCell(30).setCellValue("清货标识");
        rowField.createCell(31).setCellValue("是否重点客户");
        rowField.createCell(32).setCellValue("是否盲盒统计范围");
        rowField.createCell(33).setCellValue("是否BOX统计范围");

        int rowNumber = 1;
        SXSSFRow row;
        SXSSFCell cell1, cell21;
        while (resultSet.next()) {
            row = sheet.createRow(rowNumber);
            row.createCell(0).setCellValue(resultSet.getString("bill_number"));
            cell1 = row.createCell(1);
            cell1.setCellStyle(dateCellStyle);
            cell1.setCellValue(resultSet.getDate("fdate"));
            row.createCell(2).setCellValue(resultSet.getString("material_id_number"));
            row.createCell(3).setCellValue(resultSet.getString("material_name"));
            row.createCell(4).setCellValue(resultSet.getInt("real_quantity"));
            row.createCell(5).setCellValue(resultSet.getInt("pack_model"));
            row.createCell(6).setCellValue(resultSet.getInt("final_number"));
            row.createCell(7).setCellValue(resultSet.getDouble("tax_price"));
            row.createCell(8).setCellValue(resultSet.getDouble("all_amount_lc"));
            row.createCell(9).setCellValue(resultSet.getString("customer_id_name"));
            row.createCell(10).setCellValue(resultSet.getString("customer_group"));
            row.createCell(11).setCellValue(resultSet.getString("sale_department_id_name"));
            row.createCell(12).setCellValue(resultSet.getString("stock_id_name"));
            row.createCell(13).setCellValue(resultSet.getString("business_line"));
            row.createCell(14).setCellValue(resultSet.getString("on_or_off"));
            row.createCell(15).setCellValue(resultSet.getString("product_source"));
            row.createCell(16).setCellValue(resultSet.getString("product_series"));
            row.createCell(17).setCellValue(resultSet.getString("product_line"));
            row.createCell(18).setCellValue(resultSet.getString("ip_belong"));
            row.createCell(19).setCellValue(resultSet.getString("ip_sub"));
            row.createCell(20).setCellValue(resultSet.getString("product_type"));
            cell21 = row.createCell(21);
            cell21.setCellStyle(dateCellStyle);
            cell21.setCellValue(resultSet.getDate("final_sale_date"));
            row.createCell(22).setCellValue(resultSet.getInt("sale_days"));
            row.createCell(23).setCellValue(resultSet.getBoolean("is_new_product"));
            row.createCell(24).setCellValue(resultSet.getBoolean("is_effective"));
            row.createCell(25).setCellValue(resultSet.getString("judge_source"));
            row.createCell(26).setCellValue(resultSet.getString("data_type"));
            row.createCell(27).setCellValue(resultSet.getString("check_org"));
            row.createCell(28).setCellValue(resultSet.getDouble("material_price"));
            row.createCell(29).setCellValue(resultSet.getDouble("sale_off"));
            row.createCell(30).setCellValue(resultSet.getString("clear_flag"));
            row.createCell(31).setCellValue(resultSet.getBoolean("is_svip"));
            row.createCell(32).setCellValue(resultSet.getBoolean("blind_flag"));
            row.createCell(33).setCellValue(resultSet.getBoolean("box_flag"));
            rowNumber++;
        }

        String file = "/datadisk/javalog/dws/Report_" + endDay + "_" + sign + DateTime.now().getTime() + ".xlsx";
        if ("0".equals(testFlag)) {
            file = "D:\\test\\report_" + endDay + "_" + sign + DateTime.now().getTime() + ".xlsx";
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        workbook.write(bufferedOutputStream);
        bufferedOutputStream.close();
        fileOutputStream.close();
        preparedStatement.close();
        workbook.close();
        return new ReportBean(file, sign, dt);
    }

    public static ReportBean getGuanyi(Connection hiveConnection, String endDayInput, String testFlag, int sign) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet("管易数据");
        CellStyle dateCellStyle = workbook.createCellStyle();
        CellStyle timestampCellStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));
        timestampCellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd HH:mm:ss"));

        // 结束时间
        String endDay = endDayInput;

        // 查询分区
        String dt = DateTime.of(endDay, "yyyy-MM-dd").offset(DateField.HOUR, -24 * 1).toDateStr();

        // 开始时间 only new product
        String startDay = DateTime.of(endDay, "yyyy-MM-dd").offset(DateField.HOUR, -24 * 1).toDateStr();

        String querySQL = "SELECT * FROM dws_guanyi_details where dt='" + dt + "'";

        if ("1".equals(testFlag)) {
            querySQL = querySQL + " AND fdate<'" + endDay + "'"
                    + " AND fdate>='" + startDay + "'";
        }

        PreparedStatement preparedStatement = hiveConnection.prepareStatement(querySQL);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.setFetchSize(5000);

        SXSSFRow rowField = sheet.createRow(0);
        rowField.createCell(0).setCellValue("店铺名称");
        rowField.createCell(1).setCellValue("店铺类型");
        rowField.createCell(2).setCellValue("单据编号");
        rowField.createCell(3).setCellValue("付款日期");
        rowField.createCell(4).setCellValue("付款时间");
        rowField.createCell(5).setCellValue("订单类型");
        rowField.createCell(6).setCellValue("物料编码");
        rowField.createCell(7).setCellValue("物料名称");
        rowField.createCell(8).setCellValue("订购数量");
        rowField.createCell(9).setCellValue("盒规");
        rowField.createCell(10).setCellValue("订购数量*盒规");
        rowField.createCell(11).setCellValue("产品单价");
        rowField.createCell(12).setCellValue("标准金额");
        rowField.createCell(13).setCellValue("让利金额");
        rowField.createCell(14).setCellValue("让利后金额");
        rowField.createCell(15).setCellValue("金蝶客户名称");
        rowField.createCell(16).setCellValue("业务线平台");
        rowField.createCell(17).setCellValue("线上线下");
        rowField.createCell(18).setCellValue("产品来源");
        rowField.createCell(19).setCellValue("产品系列");
        rowField.createCell(20).setCellValue("产品线");
        rowField.createCell(21).setCellValue("IP归属");
        rowField.createCell(22).setCellValue("IP细分");
        rowField.createCell(23).setCellValue("商品类型");
        rowField.createCell(24).setCellValue("上市日期");
        rowField.createCell(25).setCellValue("上市天数");
        rowField.createCell(26).setCellValue("是否新品");
        rowField.createCell(27).setCellValue("是否统计");
        rowField.createCell(28).setCellValue("会员代码");
        rowField.createCell(29).setCellValue("会员名称");
        rowField.createCell(30).setCellValue("收货人名称");
        rowField.createCell(31).setCellValue("地区信息");
        rowField.createCell(32).setCellValue("平台商品名称");
        rowField.createCell(33).setCellValue("平台商品ID");
        rowField.createCell(34).setCellValue("赠品");
        rowField.createCell(35).setCellValue("平台代码");
        rowField.createCell(36).setCellValue("是否退款");
        rowField.createCell(37).setCellValue("是否新用户");
        rowField.createCell(38).setCellValue("首次购买时间");
        rowField.createCell(39).setCellValue("新老客"); // 首次购买单据编码
        rowField.createCell(40).setCellValue("购买产品");  // 首次购买物料编码
        rowField.createCell(41).setCellValue("创建时间");

        int rowNumber = 1;
        SXSSFRow row = null;
        SXSSFCell cell3, cell4, cell24, cell38, cell41;
        while (resultSet.next()) {
            row = sheet.createRow(rowNumber);
            row.createCell(0).setCellValue(resultSet.getString("shop_name"));
            row.createCell(1).setCellValue(resultSet.getString("group_name"));
            row.createCell(2).setCellValue(resultSet.getString("code"));
            cell3 = row.createCell(3);
            cell3.setCellStyle(dateCellStyle);
            cell3.setCellValue(resultSet.getDate("fdate"));
            cell4 = row.createCell(4);
            cell4.setCellStyle(timestampCellStyle);
            cell4.setCellValue(resultSet.getTimestamp("paytime"));
            row.createCell(5).setCellValue(resultSet.getString("order_type_name"));
            row.createCell(6).setCellValue(resultSet.getString("xproduct_item_code"));
            row.createCell(7).setCellValue(resultSet.getString("xproduct_item_name"));
            row.createCell(8).setCellValue(resultSet.getInt("xproduct_qty"));
            row.createCell(9).setCellValue(resultSet.getInt("pack_model"));
            row.createCell(10).setCellValue(resultSet.getInt("final_number"));
            row.createCell(11).setCellValue(resultSet.getDouble("xproduct_price"));
            row.createCell(12).setCellValue(resultSet.getDouble("xproduct_origin_amount"));
            row.createCell(13).setCellValue(resultSet.getDouble("xproduct_discount_fee"));
            row.createCell(14).setCellValue(resultSet.getDouble("xproduct_amount_after"));
            row.createCell(15).setCellValue(resultSet.getString("kingdee_shop_name"));
            row.createCell(16).setCellValue(resultSet.getString("business_line"));
            row.createCell(17).setCellValue(resultSet.getString("onoff"));
            row.createCell(18).setCellValue(resultSet.getString("product_source"));
            row.createCell(19).setCellValue(resultSet.getString("product_series"));
            row.createCell(20).setCellValue(resultSet.getString("product_line"));
            row.createCell(21).setCellValue(resultSet.getString("ip_belong"));
            row.createCell(22).setCellValue(resultSet.getString("ip_sub"));
            row.createCell(23).setCellValue(resultSet.getString("product_type"));
            cell24 = row.createCell(24);
            cell24.setCellStyle(dateCellStyle);
            cell24.setCellValue(resultSet.getDate("sale_date"));
            row.createCell(25).setCellValue(resultSet.getInt("sale_days"));
            row.createCell(26).setCellValue(resultSet.getBoolean("is_new"));
            row.createCell(27).setCellValue(resultSet.getBoolean("is_count"));
            row.createCell(28).setCellValue(resultSet.getString("vip_code"));
            row.createCell(29).setCellValue(resultSet.getString("vip_name"));
            row.createCell(30).setCellValue(resultSet.getString("receiver_name"));
            row.createCell(31).setCellValue(resultSet.getString("receiver_area"));
            row.createCell(32).setCellValue(resultSet.getString("xproduct_platform_item_name"));
            row.createCell(33).setCellValue(resultSet.getString("xproduct_platform_item_id"));
            row.createCell(34).setCellValue(resultSet.getBoolean("xproduct_is_gift"));
            row.createCell(35).setCellValue(resultSet.getString("platform_code"));
            row.createCell(36).setCellValue(resultSet.getInt("xproduct_refund"));
            row.createCell(37).setCellValue(resultSet.getInt("is_new_customer"));
            cell38 = row.createCell(38);
            cell38.setCellStyle(dateCellStyle);
            cell38.setCellValue(resultSet.getDate("old_fdate"));
            row.createCell(39).setCellValue(resultSet.getString("old_bill_number"));
            row.createCell(40).setCellValue(resultSet.getString("old_product_id"));
            cell41 = row.createCell(41);
            cell41.setCellStyle(timestampCellStyle);
            cell41.setCellValue(resultSet.getTimestamp("create_time"));
            rowNumber++;
        }

        // 创建XLSX文件
        String file = "/datadisk/javalog/dws/Report_guanyi" + endDay + "_" + sign + DateTime.now().getTime() + ".xlsx";
        if ("0".equals(testFlag)) {
            file = "D:\\test\\report_guanyi_" + endDay + "_" + sign + DateTime.now().getTime() + ".xlsx";
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        workbook.write(bufferedOutputStream);
        bufferedOutputStream.close();
        fileOutputStream.close();
        preparedStatement.close();
        workbook.close();
        return new ReportBean(file, sign, dt);
    }

    public static ReportBean getStock(Connection hiveConnection, String endDayInput, String testFlag, int sign) throws Exception {
        SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(1000);

        String path = "/datadisk/javalog/dws/DayReport_" + endDayInput + "_" + sign + DateTime.now().getTime() + ".xlsx";
        if ("0".equals(testFlag)) {
            path = "D:\\test\\DayReport_" + endDayInput + "_" + sign + DateTime.now().getTime() + ".xlsx";
        }

        String stockProductSourceSql =
                "SELECT * FROM ads_stock_compute_in_product_source WHERE dt ='" + endDayInput + "'";

        String stockProductSeriesSql =
                "SELECT * FROM ads_stock_compute_in_product_series WHERE dt ='" + endDayInput + "'";

        String stockProductSeriesHunSql =
                "SELECT * FROM ads_stock_compute_in_product_series_hun WHERE dt ='" + endDayInput + "'";

        String stockProductSeriesUseSql =
                "SELECT * FROM ads_stock_compute_in_product_series_use WHERE dt ='" + endDayInput + "'";

        String stockProductLineSql =
                "SELECT * FROM ads_stock_compute_in_product_line WHERE dt ='" + endDayInput + "'";

        String stockProductLineWcSql =
                "SELECT * FROM ads_stock_compute_in_product_line_wc WHERE dt ='" + endDayInput + "'";

        String stockProductLineZzSql =
                "SELECT * FROM ads_stock_compute_in_product_line_zz WHERE dt ='" + endDayInput + "'";

        String stockIPSubSql =
                "SELECT * FROM ads_stock_compute_in_ip_sub WHERE dt ='" + endDayInput + "'";

        String stockMaterialChannelSql =
                "SELECT * FROM ads_stock_compute_in_channel WHERE dt ='" + endDayInput + "'";

        String stockProductSeriesChannelSql =
                "SELECT * FROM ads_stock_compute_in_product_series_channel WHERE dt ='" + endDayInput + "'";

        String stockProductSeriesChannelHunSql =
                "SELECT * FROM ads_stock_compute_in_product_series_channel_hun WHERE dt ='" + endDayInput + "'";

        String stockProductSeriesChanneUselSql =
                "SELECT * FROM ads_stock_compute_in_product_series_channel_use WHERE dt ='" + endDayInput + "'";

        String stockMaterialGroupSql =
                "SELECT * FROM ads_stock_compute_in_fgroup_name WHERE dt ='" + endDayInput + "'";

        String stockDetailSql =
                "SELECT * FROM ads_stock WHERE dt ='" + endDayInput + "'";

        // 仓库详情
        StockDetails stockDetails = new StockDetails(xssfWorkbook, hiveConnection);
        stockDetails.setSheet("即时库存详情", stockDetailSql);

        // 产品系列
        StockProductSeries stockProductSeries = new StockProductSeries(xssfWorkbook, hiveConnection);
        stockProductSeries.setSheet("产品系列汇总", stockProductSeriesSql);
        stockProductSeries.setSheet("产品系列汇总（混装）", stockProductSeriesHunSql);
        stockProductSeries.setSheet("产品系列汇总（可用）", stockProductSeriesUseSql);

        // 产品系列渠道
        StockProductSeriesChannel stockProductSeriesChannel = new StockProductSeriesChannel(xssfWorkbook, hiveConnection);
        stockProductSeriesChannel.setSheet("产品系列渠道汇总", stockProductSeriesChannelSql);
        stockProductSeriesChannel.setSheet("产品系列渠道汇总（混装）", stockProductSeriesChannelHunSql);
        stockProductSeriesChannel.setSheet("产品系列渠道汇总（可用）", stockProductSeriesChanneUselSql);

        //产品来源
        StockProductSource stockProductSource = new StockProductSource(xssfWorkbook, hiveConnection);
        stockProductSource.setSheet("产品来源", stockProductSourceSql);

        // 产品线
        StockProductLine stockProductLine = new StockProductLine(xssfWorkbook, hiveConnection);
        stockProductLine.setSheet("产品线", stockProductLineSql);
        stockProductLine.setSheet("产品线（自主研发）", stockProductLineZzSql);
        stockProductLine.setSheet("产品线（外部采购）", stockProductLineWcSql);

        // IP细分
        StockProductIPSub stockProductIPSub = new StockProductIPSub(xssfWorkbook, hiveConnection);
        stockProductIPSub.setSheet("IP细分", stockIPSubSql);

        // 物料渠道
        StockMaterialChannel stockMaterialChannel = new StockMaterialChannel(xssfWorkbook, hiveConnection);
        stockMaterialChannel.setSheet("物料渠道", stockMaterialChannelSql);

        // 仓库分组
        StockMaterialGroup stockMaterialGroup = new StockMaterialGroup(xssfWorkbook, hiveConnection);
        stockMaterialGroup.setSheet("物料仓库分组", stockMaterialGroupSql);

        FileOutputStream fileOutputStream = new FileOutputStream(path);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        xssfWorkbook.write(bufferedOutputStream);
        bufferedOutputStream.close();
        fileOutputStream.close();
        xssfWorkbook.close();
        return new ReportBean(path, sign, endDayInput);
    }


    public static ReportBean getWeekReport(Connection hiveConnection, String endDayInput, String testFlag, int sign) throws Exception {
        SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(1000);

        String endDay = endDayInput;
        // 查询分区
        DateTime yesterday = DateTime.of(endDay, "yyyy-MM-dd").offset(DateField.HOUR, -24 * 1);
        String dt = yesterday.toString("yyyy-MM-dd");
        String month = yesterday.toString("yyyy-MM");
        String year = yesterday.toString("yyyy");

        String path = "/datadisk/javalog/dws/WeekReport_" + endDay + "_" + sign + DateTime.now().getTime() + ".xlsx";
        if ("0".equals(testFlag)) {
            path = "D:\\test\\WeekReport_" + endDay + "_" + sign + DateTime.now().getTime() + ".xlsx";
        }

        // 业务线达成
        String businessLineInReachSql = "SELECT * FROM "
                + "ads_business_line_reach_month WHERE dt = '" + month + "' AND id < 100";
        String businessLineInReachTotalSql = "SELECT * FROM "
                + "ads_business_line_reach_total WHERE dt = '" + dt + "'";
        AdsBusinessReach adsBusinessReach = new AdsBusinessReach(xssfWorkbook, hiveConnection);
        adsBusinessReach.setSheet("业务线达成-月", businessLineInReachSql, businessLineInReachTotalSql, dt);

        // 年度达成
        String businessLineInReachYearSql = "SELECT * FROM "
                + "ads_business_line_reach_year WHERE dt = '" + year + "' AND id < 100";
        AdsBusinessReachYear adsBusinessReachYear = new AdsBusinessReachYear(xssfWorkbook, hiveConnection);
        adsBusinessReachYear.setSheet("业务线达成-年", businessLineInReachYearSql, dt);

        FileOutputStream fileOutputStream = new FileOutputStream(path);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        xssfWorkbook.write(bufferedOutputStream);
        bufferedOutputStream.close();
        fileOutputStream.close();
        return new ReportBean(path, sign, endDayInput);
    }
}
