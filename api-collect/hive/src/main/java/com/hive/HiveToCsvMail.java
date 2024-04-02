package com.hive;

import cn.hutool.core.date.DateTime;
import com.bean.ReportBean;
import com.constant.ReportType;
import com.util.ConnectUtil;
import com.util.DataUtil;
import com.util.MailUtil;

import javax.mail.Session;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;


public class HiveToCsvMail {
    private static final String DATABASE = "kingdee";
    private static final String MYSQL_DATABASE = "mail";
    private static final String EMAIL_ADDRESS_QUERY_SQL = "SELECT address FROM mail.email_address WHERE is_effective = 1";

    public static void main(String[] args) throws Exception {
        String testFlag = args[0];
        String dt = args[1];
        String endDay = args[2];
        String mailFlag = args[3];

        if ("default".equals(dt)) {
            dt = DateTime.now().toDateStr();
        }
        if ("default".equals(endDay)) {
            endDay = dt;
        }

        try (Connection mysqlConnection = ConnectUtil.getMySQLConnection(MYSQL_DATABASE, testFlag);
             Connection hiveConnection = ConnectUtil.getHiveConnection(DATABASE, testFlag);
             PreparedStatement addrssSQL = mysqlConnection.prepareStatement(EMAIL_ADDRESS_QUERY_SQL)) {

//        Connection mysqlConnection = ConnectUtil.getMySQLConnection(MYSQL_DATABASE, testFlag);
//        Connection hiveConnection = ConnectUtil.getHiveConnection(DATABASE, testFlag);
//        PreparedStatement addrssSQL = mysqlConnection.prepareStatement(EMAIL_ADDRESS_QUERY_SQL);
            // 邮箱地址结果
            ResultSet addrssResultSet = addrssSQL.executeQuery();

            // 收件人
            HashSet<String> addressSet = new HashSet<>();
            while (addrssResultSet.next()) {
                addressSet.add(addrssResultSet.getString(1));
            }

            // 获取结果
            Session session = MailUtil.getSession();
            HashSet files = new HashSet<ReportBean>();

            switch (mailFlag) {
                case "report":
                    // 金蝶日详情数据
                    files.add(DataUtil.getFilePath(hiveConnection, endDay, testFlag, ReportType.DAY));

                    if ("0".equals(testFlag)) {
                        mysqlConnection.close();
                        hiveConnection.close();
                        addrssSQL.close();
                        return;
                    }

                    // 营业日报
                    files.add(DataUtil.getDayReport(hiveConnection, endDay, testFlag, ReportType.ADS_DAY));

                    // 管易详情数据
                    files.add(DataUtil.getGuanyi(hiveConnection, endDay, testFlag, ReportType.GUANYI));


                    // 金蝶周详情数据
                    if (DateTime.of(endDay, "yyyy-MM-dd").dayOfWeek() == 6) {
                        files.add(DataUtil.getFilePath(hiveConnection, endDay, testFlag, ReportType.WEEK));
                    }

                    //        if (DateTime.of(endDay, "yyyy-MM-dd").dayOfMonth() == 1) {
                    //            files.add(DataUtil.getFilePath(hiveConnection, endDay, testFlag, ReportType.MONTH));
                    //        }
                    break;
                case "stock":
                    // 即时库存
                    files.add(DataUtil.getStock(hiveConnection, endDay, testFlag, ReportType.STOCK));
                    break;
                default:
                    mysqlConnection.close();
                    hiveConnection.close();
                    addrssSQL.close();
                    return;
            }

            // 发送附件
            if ("0".equals(testFlag)) {
                mysqlConnection.close();
                hiveConnection.close();
                addrssSQL.close();
                return;
            }

            for (String mailTo : addressSet) {
                MailUtil.sendMail(session, mailTo, files, mailFlag);
            }

        } catch (Exception e) {
        }
    }
}
