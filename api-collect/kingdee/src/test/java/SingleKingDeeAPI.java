import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import com.util.FieldsUtil;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;

public class SingleKingDeeAPI {
    public static void main(String[] args) throws Exception {
        K3CloudApi client = new K3CloudApi();
        String formId = "bd_stock";
//        String formId = "sal_outstock";
        int limit = 500;

        String path = "D:\\meta\\" + formId + ".xls";
//        String fields = FieldsUtil.xls(path);
        String fields = "FNumber,FModifyDate,FCreateDate";
        String[] fieldList = fields.split(",");
        HashMap<String, String> map = FieldsUtil.xlsMap(path);

        JSONObject jsonData = new JSONObject();
        jsonData.put("FormId", formId);
        jsonData.put("FieldKeys", fields);
        jsonData.put("TopRowCount", 0);
        jsonData.put("Limit", limit);
        jsonData.put("SubSystemId", "");

        JSONArray jArray = new JSONArray();
        String dayStart = "2024-05-11";
        String dayEnd = "2024-05-12";
        String filter = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FNumber\"," +
                "\"Compare\":\"=\"," +
                "\"Value\": \"ZWSY0026\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"or\"}";
        String filter1 = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FNumber\"," +
                "\"Compare\":\"=\"," +
                "\"Value\": \"JTCX019\"," +
                "\"Right\":\"))\"," +
                "\"Logic\":\"and\"}";
        String start = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FmodifyDate\"," +
                "\"Compare\":\">=\"," +
                "\"Value\":\"" + dayStart + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"and\"}";
        String end = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FmodifyDate\"," +
                "\"Compare\":\"<\"," +
                "\"Value\":\"" + dayEnd + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"\"}";

        jArray.add(JSONObject.parse(filter));
//        jArray.add(JSONObject.parse(filter1));
//        jArray.add(JSONObject.parse(end));
//        jArray.add(JSONObject.parse(start));
        jsonData.put("FilterString", jArray);
        jsonData.put("StartRow", 0);

        while (true) {
            try {
                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());
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
                    System.out.println(result);
                }
                break;
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }
    }
}
