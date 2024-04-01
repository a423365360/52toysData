package com.toys52;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson2.JSONObject;
import com.config.ConfigInfo;
import com.util.HttpUtil;
import com.util.JsonUtil;
import com.util.SignUtil;

import java.util.ArrayList;

public class GuanyiClinetHistory {
    public static void main(String[] args) throws Exception {
        boolean testFlag = false;
        String dt, sign1, sign2, sign3, fileName, filePath;
        JSONObject jsonData1, jsonData2, jsonData3;

        String dt1 = args[0];
        String dt2 = args[1];
        DateTime dateTime = DateTime.of(dt1, "yyyy-MM-dd");
        DateTime dateTime2 = DateTime.of(dt2, "yyyy-MM-dd");

        while (dateTime.before(dateTime2)) {
            dt = dateTime.toDateStr();
            dateTime.offset(DateField.HOUR, 24);
            fileName = "gy-" + dt + "--" + DateTime.now().getTime() + ".json";
            if (testFlag) {
                filePath = "D:\\test\\" + fileName;
            } else {
                filePath = "/datadisk/gy/" + fileName;
            }

            // 归档 - 单据编号数量
            jsonData1 = JsonUtil.baseJson();
            jsonData1.put("method", "gy.erp.trade.history.get");
            jsonData1.put("start_date", dt + " 00:00:00");
            jsonData1.put("end_date", dt + " 23:59:59");
            jsonData1.put("page_size", "1");
            jsonData1.put("page_no", "1");
            sign1 = SignUtil.sign(jsonData1.toString(), ConfigInfo.SECRET);
            jsonData1.put("sign", sign1);
            int totalNumber = HttpUtil.getTotalNumber(ConfigInfo.URL, jsonData1.toString());

            // 归档 - 获取单据编号
            int pages = totalNumber / 100 + 1;
            for (int i = 1; i <= pages; i++) {
                jsonData2 = JsonUtil.baseJson();
                jsonData2.put("start_date", dt + " 00:00:00");
                jsonData2.put("end_date", dt + " 23:59:59");
                jsonData2.put("method", "gy.erp.trade.history.get");
                jsonData2.put("page_size", "100");
                jsonData2.put("page_no", String.valueOf(i));
                sign2 = SignUtil.sign(jsonData2.toString(), ConfigInfo.SECRET);
                jsonData2.put("sign", sign2);
                ArrayList<String> billNumberList = HttpUtil.getBillNumberList(ConfigInfo.URL, jsonData2.toString());

                if (billNumberList == null) {
                    continue;
                }

                // 归档 - 获取数据
                for (String billNumber : billNumberList) {
                    jsonData3 = JsonUtil.baseJson();
                    jsonData3.put("method", "gy.erp.trade.history.detail.get");
                    jsonData3.put("code", billNumber);
                    sign3 = SignUtil.sign(jsonData3.toString(), ConfigInfo.SECRET);
                    jsonData3.put("sign", sign3);
                    HttpUtil.getGuanyiData(ConfigInfo.URL, jsonData3.toString(), filePath);
                }
            }


            // 未归档 - 单据编号数量
            jsonData1 = JsonUtil.baseJson();
            jsonData1.put("method", "gy.erp.trade.get");
            jsonData1.put("start_date", dt + " 00:00:00");
            jsonData1.put("end_date", dt + " 23:59:59");
            jsonData1.put("page_size", "1");
            jsonData1.put("page_no", "1");
            sign1 = SignUtil.sign(jsonData1.toString(), ConfigInfo.SECRET);
            jsonData1.put("sign", sign1);
            totalNumber = HttpUtil.getTotalNumber(ConfigInfo.URL, jsonData1.toString());

            // 未归档 - 获取单据编号
            pages = totalNumber / 100 + 1;
            for (int i = 1; i <= pages; i++) {
                System.out.println("Page." + i);
                jsonData2 = JsonUtil.baseJson();
                jsonData2.put("start_date", dt + " 00:00:00");
                jsonData2.put("end_date", dt + " 23:59:59");
                jsonData2.put("method", "gy.erp.trade.get");
                jsonData2.put("page_size", "100");
                jsonData2.put("page_no", String.valueOf(i));
                sign2 = SignUtil.sign(jsonData2.toString(), ConfigInfo.SECRET);
                jsonData2.put("sign", sign2);
                ArrayList<String> billNumberList = HttpUtil.getBillNumberList(ConfigInfo.URL, jsonData2.toString());

                if (billNumberList == null) {
                    continue;
                }

                // 未归档 - 获取数据
                for (String billNumber : billNumberList) {
                    jsonData3 = JsonUtil.baseJson();
                    jsonData3.put("method", "gy.erp.trade.detail.get");
                    jsonData3.put("code", billNumber);
                    sign3 = SignUtil.sign(jsonData3.toString(), ConfigInfo.SECRET);
                    jsonData3.put("sign", sign3);
                    HttpUtil.getGuanyiData(ConfigInfo.URL, jsonData3.toString(), filePath);
                }
            }


            if (testFlag) {
                return;
            }

            try {
                ProcessBuilder bash = new ProcessBuilder("bash", "/home/52toys/bin/guanyi.sh", dt, filePath, fileName);
                Process process = bash.start();
                process.waitFor();
            } catch (Exception e) {
            }
        }
    }
}
