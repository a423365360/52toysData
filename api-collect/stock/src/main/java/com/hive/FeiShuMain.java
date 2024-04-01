package com.hive;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.service.FeishuCollector;
import com.service.imp.FeishuCollectorImp;

public class FeiShuMain {
    public static void main(String[] args) throws Exception {
        String testFlag = args[0];
        String operation = args[1];
        String flushDay;

        FeishuCollector feishuCollector = new FeishuCollectorImp();

        if ("update".equals(operation)) {
            feishuCollector.update(testFlag);
        } else if ("day".equals(operation)) {
            String dt = DateTime.now().offset(DateField.HOUR, -24).toDateStr();
            feishuCollector.collect(testFlag, dt);
        } else if ("flush".equals(operation)) {
            try {
                flushDay = args[2];
            } catch (Exception e) {
                return;
            }
            feishuCollector.collect(testFlag, flushDay);
        }
    }
}
