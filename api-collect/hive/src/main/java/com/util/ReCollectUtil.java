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

public class ReCollectUtil {
    public static void collect(String formId, String dt, String collectDate, String flag, String type) throws Exception {
        K3CloudApi client = new K3CloudApi();

        // Output
        String change = "";
        if ("change".equals(type)) {
            change = "change_";
        }
        long startRow = 0L;
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

        while (true) {
            try {
                // Yesterday
                String dayStart = dt;
                String dayEnd = DateTime.of(dayStart, "yyyy-MM-dd").offset(DateField.HOUR, 24).toDateStr();

                // Json
                JSONArray jArray = getJA(dayStart, dayEnd, type);
                jsonData.put("FilterString", jArray);
                jsonData.put("StartRow", startRow);

                // Get datalist
                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());

                // Complete
                if (lists.size() == 0) {
                    return;
                }

                String outPath = "/datadisk/recollect/" + change + formId + "/" + dayStart + "_at_" + collectDate + ".log";

                // TODO TEST
                if ("0".equals(flag)) {
                    outPath = "D:\\test\\" + change + formId + "_" + dayStart + "_at_" + collectDate + ".log";
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
                            value = value.toString().replace("\t", "   ").replace("\\t", "   ");
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
                bufferedWriter.close();
                Thread.sleep(1000 * 3);
            } catch (Exception e) {
                System.out.println("Error");
                Thread.sleep(1000 * 60 * 10);
            }
        }
    }

    public static void history(String formId, String flag, String type) throws Exception {
        K3CloudApi client = new K3CloudApi();

        // Output
        String change = "";
        if ("change".equals(type)) {
            change = "change_";
        }
        String redisRow = "row_history_" + change + formId;
        String redisDayKey = "history_" + change + formId;
        long startRow = 0L;
        int step = 10000;
        String metaPath = "/home/52toys/jar/meta/" + formId + ".xls";

        //TODO TEST
        if ("0".equals(flag)) {
            step = 10;
            redisRow = "test_row_history_" + formId;
            redisDayKey = "test_history_" + formId;
            metaPath = "D:\\" + formId + ".xls";
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
                // Redis
                Jedis jedis = RedisUtil.getJedis(flag);

                String dayStart = jedis.get(redisDayKey);
                String dayEnd = DateTime.of(dayStart, "yyyy-MM-dd").offset(DateField.HOUR, +24).toDateStr();
                startRow = Integer.parseInt(jedis.get(redisRow));

                if (DateTime.of(dayStart, "yyyy-MM-dd").isAfterOrEquals(DateTime.of("2023-08-01", "yyyy-MM-dd"))) {
                    jedis.close();
                    break;
                }

                // Json
                JSONArray jArray = getJA(dayStart, dayEnd, type);
                jsonData.put("FilterString", jArray);
                jsonData.put("StartRow", startRow);

                // Get datalist
                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());

                // Complete
                if (lists.size() == 0) {
                    jedis.set(redisDayKey, dayEnd);
                    jedis.set(redisRow, "0");
                    jedis.close();
                    continue;
                }

                String outPath = "/datadisk/history/" + change + formId + "/" + formId + "_" + dayStart + ".log";

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

    public static JSONArray getJA(String dayStart, String dayEnd, String type) {
        JSONArray jArray = new JSONArray();
        String fieldName = "";
        String logic = "";
        if (type.equals("create")) {
            fieldName = "FCREATEDATE";
        } else if (type.equals("approve")) {
            fieldName = "FAPPROVEDATE";
        } else if (type.equals("change")) {
            fieldName = "FAPPROVEDATE";
            logic = "and";
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
                "\"Logic\":\"" + logic +
                "\"}";
        String condition = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FCREATEDATE\"," +
                "\"Compare\":\"<\"," +
                "\"Value\":\"" + dayStart + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"\"}";
        jArray.add(JSONObject.parse(start));
        jArray.add(JSONObject.parse(end));
        if (type.equals("change")) {
            jArray.add(JSONObject.parse(condition));
        }
        return jArray;
    }
}
