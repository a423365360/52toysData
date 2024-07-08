package com.util;


import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectUtil {
    final static String OUTER_HOST = "8.140.57.104";
    final static String INNER_HOST = "172.20.116.176";
    final static String HIVE_USER = "52toys";
    final static String HIVE_PASSWORD = "52toys.qwe123.mysql";
    final static String MYSQL_USER = "root";
    final static String MYSQL_PASSWORD = "52toys.qwe123.mysql";

    public static Connection getHiveConnection(String database, String testFlag) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection connection;
        if ("0".equals(testFlag)) {
            connection = DriverManager.getConnection("jdbc:hive2://" + OUTER_HOST + ":10000/" + database, HIVE_USER, HIVE_PASSWORD);
        } else {
            connection = DriverManager.getConnection("jdbc:hive2://" + INNER_HOST + ":10000/" + database, HIVE_USER, HIVE_PASSWORD);
        }
        return connection;
    }

    public static Connection getMySQLConnection(String database, String testFlag) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection;
        if ("0".equals(testFlag)) {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                    OUTER_HOST +
                    ":3306/" + database + "?useUnicode=true&characterEncoding=utf8&useSSL=false", MYSQL_USER, MYSQL_PASSWORD);
        } else {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                    INNER_HOST +
                    ":3306/" + database + "?useUnicode=true&characterEncoding=utf8&useSSL=false", MYSQL_USER, MYSQL_PASSWORD);
        }
        return connection;
    }
}
