package com.hive;

import com.sun.mail.util.MailSSLSocketFactory;
import com.util.MailUtil;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class SugarBI {

    public static void main(String[] args) throws Exception {
        String MAIL_HOST = "imap.exmail.qq.com";
        String MAIL_PORT = "993";
        String MAIL_USERNAME = "daishanhong@52toys.com";
        String MAIL_PASSWORD = "eCSRrUsuuN7WyFAy";
        String MAIL_PROTOCOL = "imap";

        // 创建邮件
        Properties properties = new Properties();
//        properties.put("mail.smtp.host", MAIL_HOST);
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

        try {
            // 获取存储对象
            Store store = session.getStore("imap");
            store.connect(MAIL_HOST,"daishanhong@52toys.com", "eCSRrUsuuN7WyFAy");

            // 获取收件箱文件夹
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // 搜索已读邮件
            FlagTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] messages = inbox.search(flagTerm);

            for (Message message : messages) {
                // 处理邮件
                processMessage(message);
            }

            inbox.close(false);
            store.close();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void processMessage(Message message) throws Exception {
        // 获取邮件的主题
        String subject = message.getSubject();

        // 获取邮件的内容
//        Object content = message.getContent();
//
//        if (content instanceof Multipart) {
//            Multipart multipart = (Multipart) content;
//            for (int i = 0; i < multipart.getCount(); i++) {
//                BodyPart bodyPart = multipart.getBodyPart(i);
//
//                if (Part.IMAGE.equalsIgnoreCase(bodyPart.getContentType())) {
//                    // 下载图片
//                    downloadImage(bodyPart);
//                }
//            }
//        }

        System.out.println(subject);
    }

    public static void downloadImage(BodyPart bodyPart) throws Exception {
        String fileName = generateFileName(bodyPart);
        DataHandler dataHandler = bodyPart.getDataHandler();
        InputStream in = dataHandler.getInputStream();

        File file = new File(fileName);
        FileOutputStream out = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

    public static String generateFileName(BodyPart bodyPart) throws Exception {
        String fileName = bodyPart.getFileName();
        if (fileName == null) {
            fileName = "image_" + System.currentTimeMillis() + ".jpg"; // 提供一个默认的文件名
        }
        return fileName;
    }
}
