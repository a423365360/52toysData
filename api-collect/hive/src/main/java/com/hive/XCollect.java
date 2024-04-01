package com.hive;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.util.ReCollectUtil;
import com.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class XCollect {
    public static void main(String[] args) throws Exception {
        String formId = args[0];
        String year = args[1];
        String flag = args[2];
        DateTime start = DateTime.of(year + "-01-01", "yyyy-MM-dd");
        DateTime end = DateUtil.dateNew(start).offset(DateField.YEAR, 1);
        while (start.before(end)) {
            String createDate = start.toDateStr();
            String collectDate = DateTime.now().toTimestamp().toString().replace(" ", "_");
            ReCollectUtil.collect(formId, createDate, collectDate, flag, "create");
            if ("0".equals(flag)) {
                continue;
            }
            ProcessBuilder bash = new ProcessBuilder("bash", "/home/52toys/bin/recollect.sh", formId, createDate, collectDate);
            Process process = bash.start();
            int exitCode = process.waitFor();
            System.out.println(exitCode);
            start.offset(DateField.HOUR, 24);
        }
    }
}
