package com.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import com.util.FieldsUtil;
import com.util.RedisUtil;
import redis.clients.jedis.Jedis;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class UtilAPI {
    public static boolean reCollect(String table, String createDate) throws Exception {
        K3CloudApi client = new K3CloudApi();

        boolean firstFlag = true;
        String formId = table;
        String redisRow = "row_" + formId;
        String redisDayKey = formId;
        long startRow = 0L;
        int step = 10000;  // MAX 10000
        String path = "/home/52toys/jar/meta/" + formId + ".xls";

        // Get all fields
        String fields = FieldsUtil.xls(path);
        String[] fieldList = fields.split(",");

        JSONObject jsonData = new JSONObject();
        jsonData.put("FormId", formId);
        jsonData.put("FieldKeys", fields);
        jsonData.put("TopRowCount", 0);
        jsonData.put("Limit", step);
        jsonData.put("SubSystemId", "");

        while (true) {
            try {
                String dayStart = createDate;
                String dayEnd = DateTime.of(dayStart, "yyyy-MM-dd").offset(DateField.HOUR, 24).toDateStr();

                Jedis jedis = RedisUtil.getJedis("1");
                String outPath = "/datadisk/javalog/re_" + formId + "/" + dayStart + ".log";

                // TODO ing
                if (firstFlag) {
                    firstFlag = false;
                    startRow = 0L;
                } else {
                    startRow = Integer.parseInt(jedis.get(redisRow));
                }

                //System time
                JSONArray jArray = new JSONArray();
                String start = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FCREATEDATE\"," +
                        "\"Compare\":\">=\"," +
                        "\"Value\":\"" + dayStart + "T00:00:00.000\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"and\"}";
                String end = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FCREATEDATE\"," +
                        "\"Compare\":\"<\"," +
                        "\"Value\":\"" + dayEnd + "T00:00:00.000\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"\"}";
                jArray.add(JSONObject.parse(start));
                jArray.add(JSONObject.parse(end));
                jsonData.put("FilterString", jArray);

                // Set offset
                jsonData.put("StartRow", startRow);

                // Get datalist
                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());

                // No result
                if (lists.size() == 0) {
                    jedis.close();
                    System.out.println("Over");
                    return true;
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

                    //  Textually Represents
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
                System.out.println("No Server");
                Thread.sleep(1000 * 60 * 60);
            }
        }
    }
}
