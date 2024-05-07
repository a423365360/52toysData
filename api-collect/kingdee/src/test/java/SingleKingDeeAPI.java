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
        DateTime ss = DateTime.now();
        K3CloudApi client = new K3CloudApi();
        String formId = "sal_outstock";
        int limit = 10000;

        String path = "D:\\meta\\" + formId + ".xls";
        String fields = FieldsUtil.xls(path);
        String[] fieldList = fields.split(",");
//        HashMap<String, String> map = FieldsUtil.xlsMap(path);

        // 标准采购单
        String filter = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FBillTypeID\"," +
                "\"Compare\":\"StatusEqualto\"," +
                "\"Value\":\"83d822ca3e374b4ab01e5dd46a0062bd\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"and\"}";

        JSONObject jsonData = new JSONObject();
        jsonData.put("FormId", formId);
        jsonData.put("FieldKeys", fields);
        jsonData.put("TopRowCount", 0);
        jsonData.put("Limit", limit);
        jsonData.put("SubSystemId", "");
        JSONArray jArray = new JSONArray();

        String dayStart = "2024-03-01";
        String dayEnd = "2024-03-02";
        String start = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FcreateDate\"," +
                "\"Compare\":\">=\"," +
                "\"Value\":\"" + dayStart + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"and\"}";
        String end = "{\"Left\":\"(\"," +
                "\"FieldName\":\"FcreateDate\"," +
                "\"Compare\":\"<\"," +
                "\"Value\":\"" + dayEnd + "T00:00:00.000\"," +
                "\"Right\":\")\"," +
                "\"Logic\":\"\"}";
//        jArray.add(JSONObject.parse(filter));
        jArray.add(JSONObject.parse(start));
        jArray.add(JSONObject.parse(end));
        jsonData.put("FilterString", jArray);
        jsonData.put("StartRow", 0);

        while (true) {
            try {
                List<List<Object>> lists = client.executeBillQuery(jsonData.toString());
                System.out.println(lists.size());
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
        DateTime ee = DateTime.now();
        long between = ee.between(ss, DateUnit.MS);
        System.out.println("计算时长" + between);
    }
}
