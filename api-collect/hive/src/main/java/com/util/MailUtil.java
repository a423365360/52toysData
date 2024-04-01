package com.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.bean.ReportBean;
import com.constant.ReportType;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailUtil {

    private static final String MAIL_HOST = "smtp.exmail.qq.com";
    private static final String MAIL_PORT = "465";
    private static final String MAIL_USERNAME = "daishanhong@52toys.com";
    private static final String MAIL_PASSWORD = "eCSRrUsuuN7WyFAy";
    private static final String MAIL_PROTOCOL = "smtp";

    public static void sendMail(Session session, String mailTo, HashSet<ReportBean> files, String mailFlag) throws MessagingException, UnsupportedEncodingException {
        if (mailFlag == null || "".equals(mailFlag)) {
            return;
        }

        String mailName;

        switch (mailFlag) {
            case "report":
                mailName = "商品数据";
                break;
            case "stock":
                mailName = "库存数据";
                break;
            default:
                return;
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(MAIL_USERNAME));
        message.setSubject(MimeUtility.encodeText(
                DateUtil.format(DateTime.now().offset(DateField.HOUR, -24), "yyyy年M月d日") + mailName, "UTF-8", "B"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));

        MimeMultipart multipart = new MimeMultipart();

        for (ReportBean file : files) {
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(new FileDataSource(file.getFilePath())));

            String fileName = "default.csv";
            if (ReportType.DAY == file.getReportType()) {
                String dateString = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "金蝶日营业数据-" + dateString + ".xlsx";
            }
            if (ReportType.ADS_DAY == file.getReportType()) {
                String dateString = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "营业日报-" + dateString + ".xlsx";
            }
            if (ReportType.GUANYI == file.getReportType()) {
                String dateString = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "管易营业数据(新品|对标产品|自研)-" + dateString + ".xlsx";
            }
            if (ReportType.WEEK == file.getReportType()) {
                String dateString1 = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd").offset(DateField.HOUR, -6 * 24), "yyyy年M月d日");
                String dateString2 = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "金蝶周营业数据-" + dateString1 + "-" + dateString2 + ".xlsx";
            }
            if (ReportType.MONTH == file.getReportType()) {
                String dateString1 = DateUtil.format(DateUtil.parse(file.getDt().substring(0, 7) + "-01", "yyyy-MM-dd"), "yyyy年M月d日");
                String dateString2 = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "金蝶月营业数据-" + dateString1 + "-" + dateString2 + ".xlsx";
            }
            if (ReportType.STOCK == file.getReportType()) {
                String dateString = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "即时库存(测试)-" + dateString + ".xlsx";
            }

            messageBodyPart.setFileName(MimeUtility.encodeText(fileName, "UTF-8", "B"));
            multipart.addBodyPart(messageBodyPart);
        }

        message.setContent(multipart);
        Transport transport = session.getTransport();
        transport.send(message, message.getAllRecipients());
    }

    public static Session getSession() throws GeneralSecurityException {
        // 创建邮件
        Properties properties = new Properties();
        properties.put("mail.smtp.host", MAIL_HOST);
        properties.put("mail.smtp.port", MAIL_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.debug", "true");
        properties.put("mail.transport.protocol", MAIL_PROTOCOL);

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
            }
        });
        return session;
    }

    public static String extractFileName(String path) {
        Pattern pattern = Pattern.compile("[^/]+$");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }
}
