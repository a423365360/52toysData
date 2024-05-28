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
        ResultSet resultSetMaterialNumber;
        int flag;

        // 获取连接
        Connection hiveConnection = ConnectUtil.getHiveConnection(DATABASE, testFlag);

        // 查询新品、对标、自研系列和发售时间 ETL_range
        PreparedStatement ps = hiveConnection
                .prepareStatement("SELECT * FROM collect_range WHERE dt = '"
                        + doDateString + "' AND (online_sale_date <= '" + doDateString + "' OR offline_sale_date <= '" + doDateString + "') AND product_series IS NOT NULL");
        ResultSet resultSet = ps.executeQuery();

        // 查询新品、对标、自研物料编码
        PreparedStatement psMaterial = hiveConnection.prepareStatement("SELECT number FROM dim_bd_material_details WHERE product_series = ?");

        // 强制清空dwc表
        PreparedStatement truncate = hiveConnection.prepareStatement("TRUNCATE TABLE dwc_" + tableName);
        truncate.execute();

        // 写入
        int part = 1;
        while (resultSet.next()) {
            flag = resultSet.getInt("flag");
            dateOnLine0 = resultSet.getDate("online_sale_date");
            dateOnLine1 = DateUtil.dateNew(dateOnLine0).offset(DateField.HOUR, -24 * 15).toSqlDate();
            dateOnLine2 = DateUtil.dateNew(dateOnLine0).offset(DateField.HOUR, 24 * 33).toSqlDate();
            dateOffLine0 = resultSet.getDate("offline_sale_date");
            dateOffLine1 = DateUtil.dateNew(dateOffLine0).offset(DateField.HOUR, -24 * 15).toSqlDate();
            dateOffLine2 = DateUtil.dateNew(dateOffLine0).offset(DateField.HOUR, 24 * 33).toSqlDate();
            productSeries = resultSet.getString("product_series");

            // 根据系列 or 物料查询
            if (flag == 1) {

                // 去重并装载物料编码
                numberSet = new HashSet<>();

                // 根据系列反查物料编码
                psMaterial.setString(1, productSeries);
                resultSetMaterialNumber = psMaterial.executeQuery();
                while (resultSetMaterialNumber.next()) {
                    number = resultSetMaterialNumber.getString("number");
                    numberSet.add(number);
                }
                if (numberSet.size() < 1) {
                    continue;
                }

                // 组装查询条件
                where = "";
                for (String unit : numberSet) {
                    if (!where.equals("")) {
                        where = where + ",";
                    }
                    where = where + "'" + unit + "'";
                }

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
                            " WHERE ((dt >= '" + dateOnLine1 + "' AND dt < '" + dateOnLine2 + "') OR (dt >= '" + dateOffLine1 + "' AND dt < '" + dateOffLine2 + "')) AND " +
                            materialIdField + " in (" + where + ");";
                }
            } else if (flag == 2) {

                if (tableName.equals("guanyi_details")) {
                    materialIdField = "xproduct_item_name";
                    queryProductSeries = queryProductSeries + "INSERT OVERWRITE TABLE " + DATABASE + ".dwc_" + tableName +
                            " PARTITION(part='" + part + "') SELECT * FROM " + DATABASE + ".dwd_" + tableName +
                            " WHERE dt >= '" + dateOnLine1 + "' AND dt < '" + dateOnLine2 + "' AND " + materialIdField + " = '" + productSeries + "';";
                } else {
                    materialIdField = "material_name";
                    queryProductSeries = queryProductSeries + "INSERT OVERWRITE TABLE " + DATABASE + ".dwc_" + tableName +
                            " PARTITION(part='" + part + "') SELECT * FROM " + DATABASE + ".dwd_" + tableName +
                            " WHERE ((dt >= '" + dateOnLine1 + "' AND dt < '" + dateOnLine2 + "') OR (dt >= '" + dateOffLine1 + "' AND dt < '" + dateOffLine2 + "')) AND " +
                            materialIdField + " = '" + productSeries + "';";
                }
            }

            part++;
        }

        if (testFlag.equals("0")) {
            System.out.println(queryProductSeries);

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
