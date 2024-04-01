package com.hive;

import cn.hutool.core.date.DateTime;
import com.util.ReCollectUtil;
import com.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReCollect {
    public static void main(String[] args) throws Exception {
        String formId = args[0];
        String dt = args[1];
        String flag = args[2];
        String collectDate = DateTime.now().toDateStr();
        String database = "kingdee";
        try (Connection conn = Util.getConnection(database);
             PreparedStatement ps = conn.prepareStatement("select t0.x_date from (select date_format(create_date,'yyyy-MM-dd') x_date from "
                     + database + ".dwd_change_sal_outstock where dt='" + dt + "') t0 group by t0.x_date")) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String createDate = resultSet.getString("x_date");
                System.out.println(createDate);
                ReCollectUtil.collect(formId, createDate, collectDate, flag, "create");
                ProcessBuilder bash = new ProcessBuilder("bash", "/home/52toys/bin/recollect.sh", formId, createDate, collectDate);
                Process process = bash.start();
                int exitCode = process.waitFor();
                System.out.println(exitCode);
            }
        } catch (Exception e) {
        }
    }
}
