package com.hive;

import cn.hutool.core.date.DateTime;
import com.bean.ReportBean;
import com.constant.ReportType;
import com.util.ConnectUtil;
import com.util.DataUtil;
import com.util.MailUtil;

import javax.mail.Session;
import java.sql.Connection;
import java.util.HashSet;


public class HiveToCsvMail {
    private static final String DATABASE = "kingdee";
    private static final String REPORT_DATABASE = "product";
    private static final String RECEIVER = "daishanhong@52toys.com";

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

        try (Connection reportConnection = ConnectUtil.getMySQLConnection(REPORT_DATABASE, testFlag);
             Connection hiveConnection = ConnectUtil.getHiveConnection(DATABASE, testFlag)
        ) {

            // 获取结果
            HashSet<ReportBean> files = new HashSet<ReportBean>();

            switch (mailFlag) {
                case "report":
                    // 营业日报
                    files.add(DataUtil.getDayReport(hiveConnection, reportConnection, endDay, testFlag, ReportType.ADS_DAY));

                    // 管易详情数据
                    files.add(DataUtil.getGuanyi(hiveConnection, endDay, testFlag, ReportType.GUANYI));

                    // 金蝶日详情数据
                    files.add(DataUtil.getFilePath(hiveConnection, endDay, testFlag, ReportType.DAY));

                    // 周报时间判定
                    if (DateTime.of(endDay, "yyyy-MM-dd").dayOfWeek() == 6) {
                        // 金蝶周详情数据
                        files.add(DataUtil.getFilePath(hiveConnection, endDay, testFlag, ReportType.WEEK));
                    }

                    // 金蝶月详情数据
                    //        if (DateTime.of(endDay, "yyyy-MM-dd").dayOfMonth() == 1) {
                    //            files.add(DataUtil.getFilePath(hiveConnection, endDay, testFlag, ReportType.MONTH));
                    //        }

                    break;
                case "stock1":
                    // 即时库存详情
                    files.add(DataUtil.getStock(hiveConnection, endDay, testFlag, ReportType.STOCK_DETAIL));
                    break;

                case "stock2":
                    // 即时库存聚合统计
                    files.add(DataUtil.getStock(hiveConnection, endDay, testFlag, ReportType.STOCK_CAL));
                    break;

                default:
                    return;
            }

            // 发送附件
            try {
                Session session = MailUtil.getSession();
                MailUtil.sendMail(session, RECEIVER, files, mailFlag);
                System.out.println("发送成功");
            } catch (Exception e) {
                System.out.println("发送失败");
            }

        } catch (Exception e) {
            System.out.println("error");
        }
    }
}