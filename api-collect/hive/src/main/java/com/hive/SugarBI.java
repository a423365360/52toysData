package com.hive;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import javax.mail.search.SubjectTerm;
import java.io.*;
import java.util.Properties;


public class SugarBI {

    public static void main(String[] args) throws Exception {
        String MAIL_HOST = "imap.exmail.qq.com";
        String MAIL_PORT = "993";
        String MAIL_USERNAME = "daishanhong@52toys.com";
        String MAIL_PASSWORD = "eCSRrUsuuN7WyFAy";
        String MAIL_PROTOCOL = "imap";
        String doDate = DateTime.now().offset(DateField.HOUR, -24).toDateStr();

        try {
            doDate = args[0];
        } catch (Exception e) {
        }

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
            store.connect(MAIL_HOST, MAIL_USERNAME, MAIL_PASSWORD);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // 搜索已读邮件
            FlagTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            SubjectTerm subjectTerm = new SubjectTerm("sugar-" + doDate);

            Message[] messages = inbox.search(subjectTerm);

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
        Object content = message.getContent();

        Multipart multipart = (Multipart) content;
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.getDisposition() != null && bodyPart.getDisposition().equalsIgnoreCase(Part.INLINE)) {
                // 内嵌图片
                InputStream inputStream = bodyPart.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                byte[] imageData = outputStream.toByteArray();

                // 保存图片文件
                FileOutputStream fileOutputStream = new FileOutputStream("d:\\test\\test.jpg");
                fileOutputStream.write(imageData);
                fileOutputStream.close();
            }
        }

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
