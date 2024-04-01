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
import java.util.HashMap;
import java.util.List;

public class ChangeHistoryAPI {
    public static void main(String[] args) throws Exception {
        K3CloudApi client = new K3CloudApi();

        String formId = args[0];
        String localFlag = args[1];
        String redisRow = "row_change_" + formId;
        String redisDayKey = "change_" + formId;
        long startRow = 0L;
        int step = 10000;  // MAX 10000
        String path = "/home/52toys/jar/meta/" + formId + ".xls";

        //TODO TEST
        if ("0".equals(localFlag)) {
            step = 10000;
            redisRow = "test_row_change_" + formId;
            redisDayKey = "test_change_" + formId;
            path = "D:\\" + formId + ".xls";
        }

        // Get all fields
        String fields = FieldsUtil.xls(path);
        String[] fieldList = fields.split(",");
        HashMap<String, String> map = FieldsUtil.xlsMap(path);

        JSONObject jsonData = new JSONObject();
        jsonData.put("FormId", formId);
        jsonData.put("FieldKeys", fields);
        jsonData.put("TopRowCount", 0);
        jsonData.put("Limit", step);
        jsonData.put("SubSystemId", "");

        while (true) {
            try {
                Jedis jedis = RedisUtil.getJedis(localFlag);
                String dayStart = jedis.get(redisDayKey);
                if ("2023-07-27".equals(dayStart)) {
                    jedis.close();
                    break;
                }
                String dayEnd = DateTime.of(dayStart, "yyyy-MM-dd").offset(DateField.HOUR, 24).toDateStr();
                startRow = Integer.parseInt(jedis.get(redisRow));
                System.out.println("dayStart: " + dayStart);
                System.out.println("dayEnd: " + dayEnd);

                String outPath = "/datadisk/javalog/change_" + formId + "/" + formId + "_" + dayStart + ".log";
                // TODO TEST
                if ("0".equals(localFlag)) {
                    outPath = "D:\\test\\" + formId + "_" + dayStart + ".log";
                }

                //System time
                JSONArray jArray = new JSONArray();
                String start = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FAPPROVEDATE\"," +
                        "\"Compare\":\">=\"," +
                        "\"Value\":\"" + dayStart + "T00:00:00.000\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"and\"}";
                String end = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FAPPROVEDATE\"," +
                        "\"Compare\":\"<\"," +
                        "\"Value\":\"" + dayEnd + "T00:00:00.000\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"and\"}";
                String condition = "{\"Left\":\"(\"," +
                        "\"FieldName\":\"FCREATEDATE\"," +
                        "\"Compare\":\"<\"," +
                        "\"Value\":\"" + dayStart + "T00:00:00.000\"," +
                        "\"Right\":\")\"," +
                        "\"Logic\":\"\"}";
                jArray.add(JSONObject.parse(start));
                jArray.add(JSONObject.parse(end));
                jArray.add(JSONObject.parse(condition));
                jsonData.put("FilterString", jArray);

                // Set offset
                jsonData.put("StartRow", startRow);

                // Get datalist
                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());

                // No result reset
                if (lists.size() == 0) {
                    jedis.set(redisRow, "0");
                    jedis.set(redisDayKey, dayEnd);
                    jedis.close();
                    continue;
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
                    if ("0".equals(localFlag)) {
                        System.out.println(data);
                    }

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
