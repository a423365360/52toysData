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
import java.util.HashSet;


public class HiveToCsvMail {
    private static final String DATABASE = "kingdee";
    private static final String MAIL_DATABASE = "mail";
    private static final String REPORT_DATABASE = "product";
    private static final String EMAIL_ADDRESS_QUERY_SQL = "SELECT address FROM mail.email_address WHERE is_effective = 1";
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
//             Connection mailConnection = ConnectUtil.getMySQLConnection(MAIL_DATABASE, testFlag)
//             PreparedStatement addrssSQL = mailConnection.prepareStatement(EMAIL_ADDRESS_QUERY_SQL)
        ) {

            // 邮箱地址
//            ResultSet addrssResultSet = addrssSQL.executeQuery();
//            HashSet<String> addressSet = new HashSet<>();
//            while (addrssResultSet.next()) {
//                addressSet.add(addrssResultSet.getString(1));
//            }

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


//            // TODO 并发报表
//            ExecutorService pool = Executors.newFixedThreadPool(3);
//            CountDownLatch latch = new CountDownLatch(addressSet.size());
//            for (String mailTo : addressSet) {
//                pool.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Session session = MailUtil.getSession();
//                            MailUtil.sendMail(session, mailTo, files, mailFlag);
//                            latch.countDown(); // Decrement the latch counter once the email is sent
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
//            }
//
//            // Wait for all email sending tasks to complete before terminating the main thread
//            try {
//                latch.await(); // Block the main thread until the latch count reaches zero
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                pool.shutdown(); // Gracefully shut down the thread pool
//            }
//
//            System.out.println("Main thread exiting");

        } catch (Exception e) {
        }
    }
}