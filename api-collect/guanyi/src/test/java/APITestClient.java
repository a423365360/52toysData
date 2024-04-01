import com.alibaba.fastjson2.JSONObject;
import com.config.ConfigInfo;
import com.util.HttpUtil;
import com.util.SignUtil;

import java.io.*;


public class APITestClient {
    public APITestClient() {
    }

    public static void main(String[] args) throws Exception {
        JSONObject jsonData = new JSONObject();
        jsonData.put("appkey", ConfigInfo.APPKEY);
        jsonData.put("sessionkey", ConfigInfo.SESSIONKEY);
//        jsonData.put("method", "gy.erp.trade.history.get");
        jsonData.put("method", "gy.erp.trade.history.detail.get");
        jsonData.put("code", "SDO663013839144");
//        jsonData.put("cancel", true);
//        jsonData.put("start_date", "2023-01-01 00:00:00");
//        jsonData.put("end_date", "2023-01-02 00:00:00");
//        jsonData.put("end_date", "2023-01-01 23:59:59");
//        jsonData.put("page_size", "100");
//        jsonData.put("page_no", "1");
        System.out.println(jsonData);

        // 获取签名
        String sign = SignUtil.sign(jsonData.toString(), ConfigInfo.SECRET);

        // 配置签名
        jsonData.put("sign", sign);

        // 调用方法
        HttpUtil.sendPost(ConfigInfo.URL, jsonData.toString());
    }

    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;

            while ((s = br.readLine()) != null) {
                result.append(s);
            }

            br.close();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return result.toString();
    }

    public static void string2Txt(File file, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(content);
            writer.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
