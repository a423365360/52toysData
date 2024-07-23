package com.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.bean.ReportBean;
import com.constant.ReportType;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
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
    private static final int CHUNK_SIZE = 1024 * 1024; // 1 MB chunk size

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
            case "stock1":
                mailName = "库存数据（详情明细）";
                break;
            case "stock2":
                mailName = "库存数据（聚合统计）";
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
//            messageBodyPart.setDataHandler(new DataHandler(new FileDataSource(file.getFilePath())));
            messageBodyPart.setDataHandler(new DataHandler(new ChunkedFileDataSource(file.getFilePath())));

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
                fileName = "管易营业数据(新品,对标,自研)-" + dateString + ".xlsx";
            }
            if (ReportType.WEEK == file.getReportType()) {
                String dateString1 = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd").offset(DateField.HOUR, -6 * 24), "yyyy年M月d日");
                String dateString2 = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "金蝶周营业数据-" + dateString1 + "-" + dateString2 + ".xlsx";
            }
//            if (ReportType.MONTH == file.getReportType()) {
//                String dateString1 = DateUtil.format(DateUtil.parse(file.getDt().substring(0, 7) + "-01", "yyyy-MM-dd"), "yyyy年M月d日");
//                String dateString2 = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
//                fileName = "金蝶月营业数据-" + dateString1 + "-" + dateString2 + ".xlsx";
//            }
            if (ReportType.STOCK_DETAIL == file.getReportType()) {
                String dateString = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "即时库存（详情明细）-" + dateString + ".xlsx";
            }
            if (ReportType.STOCK_CAL == file.getReportType()) {
                String dateString = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "即时库存（聚合统计）-" + dateString + ".xlsx";
            }
            if (ReportType.WEEK_REPORT == file.getReportType()) {
                String dateString = DateUtil.format(DateUtil.parse(file.getDt(), "yyyy-MM-dd"), "yyyy年M月d日");
                fileName = "营业周报-" + dateString + ".xlsx";
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

    // Custom DataSource implementation to handle chunked file transfer
    static class ChunkedFileDataSource implements DataSource {

        private final String filePath;

        public ChunkedFileDataSource(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ChunkedInputStream(filePath);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }
    }

    // Custom InputStream implementation to read file in chunks
    static class ChunkedInputStream extends InputStream {

        private final String filePath;
        private final byte[] buffer;
        private long remainingBytes;
        private FileInputStream fileInputStream;

        public ChunkedInputStream(String filePath) throws IOException {
            this.filePath = filePath;
            this.buffer = new byte[CHUNK_SIZE];
            this.remainingBytes = new java.io.File(filePath).length();
            this.fileInputStream = new FileInputStream(filePath);
        }

        @Override
        public int read() throws IOException {
            if (remainingBytes == 0) {
                return -1;
            }

            int bytesRead = fileInputStream.read(buffer);
            if (bytesRead == -1) {
                return -1;
            }

            remainingBytes -= bytesRead;
            return bytesRead;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remainingBytes == 0) {
                return -1;
            }

            int bytesToRead = Math.min(len, (int) remainingBytes);
            int bytesRead = fileInputStream.read(b, off, bytesToRead);
            if (bytesRead == -1) {
                return -1;
            }

            remainingBytes -= bytesRead;
            return bytesRead;
        }

        @Override
        public void close() throws IOException {
            fileInputStream.close();
        }
    }
}

