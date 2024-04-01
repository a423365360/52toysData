package com.hive;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.util.ConnectUtil;

import java.sql.*;
import java.util.HashSet;

public class Range {
    private static String DATABASE = "kingdee";

    public static void main(String[] args) throws Exception {
        String testFlag = args[0];
        String tableName = args[1];
        Date dateOnLine0, dateOnLine1, dateOnLine2, dateOffLine0, dateOffLine1, dateOffLine2;
        Date today = DateTime.now().toSqlDate();
        Date doDate = DateUtil.dateNew(today).offset(DateField.HOUR, -24).toSqlDate();
        String doDateString = doDate.toString();
        String productSeries, number, where;
        String queryProductSeries = "";
        HashSet<String> numberSet;
        String materialIdField;

        // 获取连接
        Connection hiveConnection = ConnectUtil.getHiveConnection(DATABASE, testFlag);

        // 查询新品、对标、自研系列和发售时间
        PreparedStatement ps = hiveConnection.prepareStatement("SELECT product_series, first_sale_date, online_sale_date, offline_sale_date, create_time FROM ETL_range WHERE dt = '"
                + doDateString + "' AND (online_sale_date <= '" + doDateString + "' OR offline_sale_date <= '" + doDateString + "') AND product_series IS NOT NULL");
        ResultSet resultSet = ps.executeQuery();

        PreparedStatement psMaterial = hiveConnection.prepareStatement("SELECT number FROM dim_bd_material_details WHERE product_series = ?");

        // 强制清空dwc表

        PreparedStatement truncate = hiveConnection.prepareStatement("TRUNCATE TABLE dwc_" + tableName);
        if ("1".equals(testFlag)) {
            truncate.execute();
        }

        // 写入
        int part = 1;
        while (resultSet.next()) {

            numberSet = new HashSet<>();
            dateOnLine0 = resultSet.getDate("online_sale_date");
            dateOnLine1 = DateUtil.dateNew(dateOnLine0).offset(DateField.HOUR, -24 * 15).toSqlDate();
            dateOnLine2 = DateUtil.dateNew(dateOnLine0).offset(DateField.HOUR, 24 * 33).toSqlDate();
            dateOffLine0 = resultSet.getDate("offline_sale_date");
            dateOffLine1 = DateUtil.dateNew(dateOffLine0).offset(DateField.HOUR, -24 * 15).toSqlDate();
            dateOffLine2 = DateUtil.dateNew(dateOffLine0).offset(DateField.HOUR, 24 * 33).toSqlDate();

            // 根据系列反查物料编码
            productSeries = resultSet.getString("product_series");
            psMaterial.setString(1, productSeries);
            ResultSet resultSetMaterialNumber = psMaterial.executeQuery();
            while (resultSetMaterialNumber.next()) {
                number = resultSetMaterialNumber.getString("number");
                numberSet.add(number);
            }
            if (numberSet.size() < 1) {
                continue;
            }

            // 组装查询条件
            where = "'<--default-->'";
            for (String unit : numberSet) {
                where = where + ",'" + unit + "'";
            }

            // 管易只采用线上发售日期
            if (tableName.equals("guanyi_details")) {
                materialIdField = "xproduct_item_code";
                queryProductSeries = queryProductSeries + "INSERT OVERWRITE TABLE " + DATABASE + ".dwc_" + tableName +
                        " PARTITION(part='" + part + "') SELECT * FROM " + DATABASE + ".dwd_" + tableName +
                        " WHERE dt >= '" + dateOnLine1 + "' AND dt < '" + dateOnLine2 + "' AND " + materialIdField + " in ("
                        + where + ");";
            } else {
                materialIdField = "material_id_number";
                queryProductSeries = queryProductSeries + "INSERT OVERWRITE TABLE " + DATABASE + ".dwc_" + tableName +
                        " PARTITION(part='" + part + "') SELECT * FROM " + DATABASE + ".dwd_" + tableName +
                        " WHERE ((dt >= '" + dateOnLine1 + "' AND dt < '" + dateOnLine2 + "') OR (dt >= '" + dateOffLine1 + "' AND dt < '" + dateOffLine2 + "')) AND " + materialIdField + " in ("
                        + where + ");";
            }

            part++;

        }

        // TODO TEST
        if ("0".equals(testFlag)) {
            System.out.println(queryProductSeries);
            ps.close();
            psMaterial.close();
            truncate.close();
            hiveConnection.close();
            return;
        }

        // 写入dwc表
        ProcessBuilder bash = new ProcessBuilder("/opt/module/hive-3.1.2/bin/hive", "-e", queryProductSeries);
        Process process = bash.start();
        process.waitFor();

        // 关闭连接
        if (ps != null) {
            ps.close();
        }
        if (psMaterial != null) {
            psMaterial.close();
        }
        if (truncate != null) {
            truncate.close();
        }
        if (hiveConnection != null) {
            hiveConnection.close();
        }
    }
}