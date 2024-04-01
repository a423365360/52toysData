import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import com.util.FieldsUtil;
import org.junit.Assert;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class RMB {
    static String kdwebapi = "kdwebapi.properties";

    public static void main(String[] args) throws Exception {

        InputStream resourceStream = RMB.class.getClassLoader().getResourceAsStream(kdwebapi);
        Properties property = new Properties();
        property.load(resourceStream);
        String fuse = property.getProperty("X-KDApi-AcctID");
        if ("20191211084533058".equals(fuse)) {
            System.out.println("Pro Environment");
            return;
        }

        K3CloudApi client = new K3CloudApi();
        String formId = "BD_Rate";
        int limit = 2;
        String path = "D:\\meta\\" + formId + ".xls";
        String fields = FieldsUtil.xls(path);
        String[] fieldList = fields.split(",");
//        HashMap<String, String> map = FieldsUtil.xlsMap(path);

        String filter = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FCloseStatus\"," +
                "\"Compare\":\"=\"," +
                "\"Value\":\"B\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"\"}";

        JSONObject jsonData = new JSONObject();
        jsonData.put("FormId", formId);
        jsonData.put("FieldKeys", fields);
        jsonData.put("TopRowCount", 0);
        jsonData.put("Limit", limit);
        jsonData.put("SubSystemId", "");
        JSONArray jArray = new JSONArray();

        String dayStart = "2024-01-01";
        String dayEnd = "2024-01-03";
        String start = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FCreateDate\"," +
                "\"Compare\":\">=\"," +
                "\"Value\":\"" + dayStart + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"and\"}";
        String end = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FCreateDate\"," +
                "\"Compare\":\"<\"," +
                "\"Value\":\"" + dayEnd + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"\"}";
        jArray.add(JSONObject.parse(start));
        jArray.add(JSONObject.parse(end));
//        jArray.add(JSONObject.parse(filter));
        jsonData.put("FilterString", jArray);
        jsonData.put("StartRow", 0);

        while (true) {
            try {
                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());
                System.out.println(lists);
                for (int i = 0; i < lists.size(); i++) {
                    List<Object> row = lists.get(i);
                    JSONObject result = new JSONObject();
                    for (int j = 0; j < row.size(); j++) {
                        Object value = row.get(j);

                        if (value == null) {
                            value = "null";
                        }

//                        result.put(map.get(fieldList[j]) + " ------- " + fieldList[j], value);
                        result.put(fieldList[j].replace(".F", "_F"), value);
                    }
//                    System.out.println(result);
                }
                break;
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }
    }
}
