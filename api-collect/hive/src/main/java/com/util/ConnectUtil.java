package com.util;


import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectUtil {
    public static Connection getHiveConnection(String database) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection connection = DriverManager.getConnection("jdbc:hive2://8.140.57.104:10000/" + database, "52toys", "52toys.qwe123.mysql");
//        Connection connection = DriverManager.getConnection("jdbc:hive2://172.20.116.176:10000/" + database, "52toys", "52toys.qwe123.mysql");
        return connection;
    }

    public static Connection getHiveConnection(String database, String testFlag) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection connection = null;
        if ("0".equals(testFlag)) {
            connection = DriverManager.getConnection("jdbc:hive2://8.140.57.104:10000/" + database, "52toys", "52toys.qwe123.mysql");
        } else {
            connection = DriverManager.getConnection("jdbc:hive2://172.20.116.176:10000/" + database, "52toys", "52toys.qwe123.mysql");
        }
        return connection;
    }

    public static Connection getMySQLConnection(String database, String testFlag) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = null;
        if ("0".equals(testFlag)) {
            connection = DriverManager.getConnection("jdbc:mysql://8.140.57.104:3306/" + database + "?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "52toys.qwe123.mysql");
        } else {
            connection = DriverManager.getConnection("jdbc:mysql://172.20.116.176:3306/" + database + "?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "52toys.qwe123.mysql");
        }
        return connection;
    }
}
