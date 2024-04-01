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


public class KingDeeHistoryAPI {
    public static void main(String[] args) throws Exception {
        K3CloudApi client = new K3CloudApi();

        String formId = args[0];
        String localFlag = args[1];
        String redisKey = "history_" + formId;
        String redisRow = "row_history_" + formId;

        long startRow = 0L;
        int step = 10000;
        String path = "/home/52toys/jar/meta/" + formId + ".xls";

        //TODO TEST
        if ("0".equals(localFlag)) {
            step = 1000;
            path = "D:\\" + formId + ".xls";
            redisKey = "test_history_" + formId;
            redisRow = "test_row_history_" + formId;
        }

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
                Jedis jedis = RedisUtil.getJedis(localFlag);

                String dayStart = jedis.get(redisKey);

                if (DateTime.of(dayStart, "yyyy-MM-dd").isAfterOrEquals(DateTime.of("2023-07-05", "yyyy-MM-dd"))) {
                    jedis.close();
                    break;
                }

                String dayEnd = DateTime.of(dayStart, "yyyy-MM-dd").offset(DateField.MONTH, 1).toDateStr();
                startRow = Integer.parseInt(jedis.get(redisRow));

                String file_name = formId + "-" + dayStart;
                String outPath = "/datadisk/history/" + formId + "/" + file_name + ".log";

                if ("0".equals(localFlag)) {
                    outPath = "D:\\history\\" + formId + "_" + file_name + ".log";
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
                jsonData.put("StartRow", startRow);

                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());

                if (lists.size() == 0) {
                    jedis.set(redisKey, dayEnd);
                    jedis.set(redisRow, "0");
                    jedis.close();
                    continue;
                }

                int number = lists.size();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath, true), "utf-8"));

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

                    if ("0".equals(localFlag)) {
                        System.out.println(data);
                    }

                    bufferedWriter.write(data.toString());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }

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
