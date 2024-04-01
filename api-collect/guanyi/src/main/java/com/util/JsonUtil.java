package com.util;

import com.alibaba.fastjson2.JSONObject;
import com.config.ConfigInfo;

public class JsonUtil {
    public static JSONObject baseJson() {
        JSONObject jsonData = new JSONObject();
        jsonData.put("appkey", ConfigInfo.APPKEY);
        jsonData.put("sessionkey", ConfigInfo.SESSIONKEY);
        return jsonData;
    }
}
