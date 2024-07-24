package com.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import redis.clients.jedis.Jedis;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CollectUtil {
    public static void collectYesterday(String formId, String flag, String type) throws Exception {
        K3CloudApi client = new K3CloudApi();

        // Output
        String change = "";
        if ("change".equals(type)) {
            change = "change_";
        }
        String redisRow = "row_" + change + formId;
        String redisDayKey = change + formId;
        long startRow = 0L;
        int step = 10000;
        String metaPath = "/home/52toys/jar/meta/" + formId + ".xls";

        //TODO TEST
        if ("0".equals(flag)) {
            step = 10;
            redisRow = "test_row_" + change + formId;
            redisDayKey = "test_" + change + formId;
            metaPath = "D:\\test\\" + formId + ".xls";
        }

        // Get all fields
        String fields = FieldsUtil.xls(metaPath);
        String[] fieldList = fields.split(",");
        JSONObject jsonData = new JSONObject();
        jsonData.put("FormId", formId);
        jsonData.put("FieldKeys", fields);
        jsonData.put("TopRowCount", 0);
        jsonData.put("Limit", step);
        jsonData.put("SubSystemId", "");

        while (true) {
            try {
                // Yesterday
                String dayEnd = DateTime.now().toDateStr();
                String dayStart = DateTime.of(dayEnd, "yyyy-MM-dd").offset(DateField.HOUR, -24).toDateStr();

                // Redis
                Jedis jedis = RedisUtil.getJedis(flag);

                // Check new day
                String redisDayValue = jedis.get(redisDayKey);
                if (dayStart.equals(redisDayValue)) {
                    startRow = Integer.parseInt(jedis.get(redisRow));
                } else {
                    startRow = 0L;
                    jedis.set(redisRow, "0");
                    jedis.set(redisDayKey, dayStart);
                }

                // Json
                JSONArray jArray = getJA(dayStart, dayEnd, type);
                jsonData.put("FilterString", jArray);
                jsonData.put("StartRow", startRow);

                // Get datalist
                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());

                // Complete
                if (lists.size() == 0) {
                    jedis.close();
                    return;
                }

                String outPath = "/datadisk/javalog/" + change + formId + "/" + formId + "_" + dayStart + ".log";

                // TODO TEST
                if ("0".equals(flag)) {
                    outPath = "D:\\test\\" + change + formId + "_" + dayStart + ".log";
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath, true), "utf-8"));

                // Split result
                int number = lists.size();
                for (int i = 0; i < number; i++) {
                    List<Object> row = lists.get(i);
                    JSONObject data = new JSONObject();
                    for (int j = 0; j < row.size(); j++) {
                        Object value = row.get(j);
                        if (value instanceof String) {
                            value = value.toString().replace("\t", "   ");
                        }
                        data.put(fieldList[j].replace(".F", "_F"), value);
                    }
                    data.put("API_timestamp", DateTime.now());

                    //TODO Test print
                    if ("0".equals(flag)) {
                        System.out.println(data);
                    }

                    // Textually Represents
                    bufferedWriter.write(data.toString());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }

                // Next
                startRow += number;
                jedis.set(redisRow, String.valueOf(startRow));
                jedis.close();
                bufferedWriter.close();
                Thread.sleep(1000 * 3);
            } catch (Exception e) {
                System.out.println("Error");
                Thread.sleep(1000 * 60 * 10);
            }
        }
    }


    public static void replaceHistory(String formId, String flag, String type, String startDate, String endDate, String table) throws Exception {
        K3CloudApi client = new K3CloudApi();

        DateTime current = DateTime.of(startDate, "yyyy-MM-dd");
        DateTime end = DateTime.of(endDate, "yyyy-MM-dd");

        String fileName, filePath, dt, dayStart, dayEnd;
        List<List<Object>> lists;

        long startRow;
        int step = 10000;
        String metaPath = "/home/52toys/jar/meta/" + formId + ".xls";

        //TODO TEST
        if ("0".equals(flag)) {
            step = 100;
            metaPath = "D:\\meta\\" + formId + ".xls";
        }

        // Get all fields
        String fields = FieldsUtil.xls(metaPath);
        String[] fieldList = fields.split(",");
        JSONObject jsonData = new JSONObject();
        jsonData.put("FormId", formId);
        jsonData.put("FieldKeys", fields);
        jsonData.put("TopRowCount", 0);
        jsonData.put("Limit", step);
        jsonData.put("SubSystemId", "");

        // Date
        while (current.before(end)) {
            startRow = 0L;

            dt = DateTime.of(current).toDateStr();
            dayStart = dt;
            dayEnd = DateTime.of(dayStart, "yyyy-MM-dd").offset(DateField.HOUR, +24).toDateStr();

            jsonData.put("FilterString", getJA(dayStart, dayEnd, type));

            fileName = table + "_" + dt + "_" + DateTime.now().getTime() + ".log";
            filePath = "/datadisk/history/" + fileName;

            // TODO TEST
            if ("0".equals(flag)) {
                filePath = "D:\\test\\" + fileName;
            }

            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(filePath, true), "utf-8"));
            // Row
            while (true) {
                try {
                    // Json
                    JSONArray jArray = getJA(dayStart, dayEnd, type);
                    jsonData.put("FilterString", jArray);
                    jsonData.put("StartRow", startRow);

                    // Get datalist
                    lists = client.executeBillQuery(jsonData.toString());

                    // Complete
                    if (lists.size() == 0) {
                        break;
                    }

                    // Split result
                    int number = lists.size();
                    for (int i = 0; i < number; i++) {
                        List<Object> row = lists.get(i);
                        JSONObject data = new JSONObject();
                        for (int j = 0; j < row.size(); j++) {
                            Object value = row.get(j);
                            if (value instanceof String) {
                                value = value.toString().replace("\t", "   ");
                            }
                            data.put(fieldList[j].replace(".F", "_F"), value);
                        }
                        data.put("API_timestamp", DateTime.now());

                        //TODO Test Print
                        if ("0".equals(flag)) {
                            System.out.println(data);
                        }

                        // Textually Represents
                        bufferedWriter.write(data.toString());
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }

                    // Next
                    startRow += number;
                } catch (Exception e) {
                    System.out.println("Error");
                }
            }
            bufferedWriter.close();
            current.offset(DateField.HOUR, 24);

            // Shell
            ProcessBuilder bash = new ProcessBuilder("/home/52toys/bin/flush-history.sh", table, dt, filePath, fileName);
            Process process = bash.start();
            process.waitFor();
        }
    }

    // 昨日采集
    public static void day(String formId, String flag, String type, String startDate, String table) throws Exception {
        K3CloudApi client = new K3CloudApi();

        String fileName, filePath, dayStart, dayEnd;
        List<List<Object>> lists;

        long startRow;
        int step = 10000;
        String metaPath = "/home/52toys/jar/meta/" + formId + ".xls";

        //TODO TEST
        if ("0".equals(flag)) {
            step = 10000;
            metaPath = "D:\\meta\\" + formId + ".xls";
        }

        // Get all fields
        String fields = FieldsUtil.xls(metaPath);
        String[] fieldList = fields.split(",");
        JSONObject jsonData = new JSONObject();
        jsonData.put("FormId", formId);
        jsonData.put("FieldKeys", fields);
        jsonData.put("TopRowCount", 0);
        jsonData.put("Limit", step);
        jsonData.put("SubSystemId", "");

        // Date
        startRow = 0L;

        dayStart = startDate;
        dayEnd = DateTime.of(dayStart, "yyyy-MM-dd").offset(DateField.HOUR, +24).toDateStr();

//        jsonData.put("FilterString", getJA(dayStart, dayEnd, type));

        fileName = table + "_" + dayStart + "_" + DateTime.now().getTime() + ".log";
        filePath = "/datadisk/javalog/day/" + fileName;

        // TODO TEST
        if ("0".equals(flag)) {
            filePath = "D:\\test\\" + fileName;
        }

        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath, true), "utf-8"));
        // Row
        while (true) {
            // Json
            JSONArray jArray = getJA(dayStart, dayEnd, type);
            jsonData.put("FilterString", jArray);
            jsonData.put("StartRow", startRow);

            // Get datalist
            lists = client.executeBillQuery(jsonData.toString());

            // Complete
            if (lists.size() == 0) {
                break;
            }

            // Split result
            int number = lists.size();
            for (int i = 0; i < number; i++) {
                List<Object> row = lists.get(i);
                JSONObject data = new JSONObject();
                for (int j = 0; j < row.size(); j++) {
                    Object value = row.get(j);
                    if (value instanceof String) {
                        value = value.toString().replace("\t", "   ");
                    }
                    data.put(fieldList[j].replace(".F", "_F"), value);
                }
                data.put("API_timestamp", DateTime.now());

                //TODO Test Print
                if ("0".equals(flag)) {
                    System.out.println(data);
                }

                // Textually Represents
                bufferedWriter.write(data.toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

            // Next
            startRow += number;
        }
        bufferedWriter.close();

        // Shell
        if ("0".equals(flag)) {
            return;
        }
        ProcessBuilder bash = new ProcessBuilder("/home/52toys/bin/flush-day.sh", table, dayStart, filePath);
        Process process = bash.start();
        process.waitFor();
    }

    public static JSONArray getJA(String dayStart, String dayEnd, String type) {
        JSONArray jArray = new JSONArray();
        String fieldName = "", billType = "", open = "", cutTime = "", orgUse = "", orgUse1 = "",isEnable = "";
        switch (type) {
            case "create":
                fieldName = "FCREATEDATE";
                break;
            case "approve":
                fieldName = "FAPPROVEDATE";
                break;
            case "audit":
                fieldName = "FAuditDate";
                break;
            case "modify":
                fieldName = "FModifyDate";
//                orgUse = "{\"Left\":\"(\"," +
//                        "\"FieldName\":\"FUseOrgId.FNumber\"," +
//                        "\"Compare\":\"=\"," +
//                        "\"Value\":\"10000\"," +
//                        "\"Right\":\")\"," +
//                        "\"Logic\":\"and\"}";
//                jArray.add(JSONObject.parse(orgUse));
                break;
            case "modify0":
                fieldName = "FModifyDate";
                orgUse = "{\"Left\":\"((\"," +
                        "\"FieldName\":\"FUseOrgId.FNumber\"," +
                        "\"Compare\":\"=\"," +
                        "\"Value\":\"10000\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"or\"}";
                // 致物盛源独有物料
                orgUse1 = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FUseOrgId.FNumber\"," +
                        "\"Compare\":\"=\"," +
                        "\"Value\":\"50001\"," +
                        "\"Right\":\"))\"," +
                        "\"Logic\":\"and\"}";
                isEnable = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FIsEnable\"," +
                        "\"Compare\":\"=\"," +
                        "\"Value\": true," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"and\"}";
                jArray.add(JSONObject.parse(orgUse));
                jArray.add(JSONObject.parse(orgUse1));
                jArray.add(JSONObject.parse(isEnable));
                break;
            case "pur":
                fieldName = "FModifyDATE";
                // 标准采购单
                billType = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FBillTypeID\"," +
                        "\"Compare\":\"StatusEqualto\"," +
                        "\"Value\":\"83d822ca3e374b4ab01e5dd46a0062bd\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"and\"}";
                jArray.add(JSONObject.parse(billType));
                break;
            case "open":
                // 未关闭
                open = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FCloseStatus\"," +
                        "\"Compare\":\"StatusEqualto\"," +
                        "\"Value\":\"A\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"and\"}";
                // 标准采购单
                billType = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FBillTypeID\"," +
                        "\"Compare\":\"StatusEqualto\"," +
                        "\"Value\":\"83d822ca3e374b4ab01e5dd46a0062bd\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"and\"}";
                // 时间范围
                cutTime = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FCREATEDATE\"," +
                        "\"Compare\":\">=\"," +
                        "\"Value\":\"2023-01-01T00:00:00.000\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"\"}";

                jArray.add(JSONObject.parse(open));
                jArray.add(JSONObject.parse(billType));
                jArray.add(JSONObject.parse(cutTime));
                return jArray;
        }

        String start = "{\"Left\":\"(\"," +
                "\"FieldName\":\"" +
                fieldName +
                "\"," +
                "\"Compare\":\">=\"," +
                "\"Value\":\"" + dayStart + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"and\"}";
        String end = "{\"Left\":\"(\"," +
                "\"FieldName\":\"" +
                fieldName +
                "\"," +
                "\"Compare\":\"<\"," +
                "\"Value\":\"" + dayEnd + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"\"}";

        jArray.add(JSONObject.parse(start));
        jArray.add(JSONObject.parse(end));

        return jArray;
    }
}
