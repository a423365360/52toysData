package com.hive;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.net.HttpCookie;
import java.util.List;
import java.util.Properties;

public class StockCollect {
    private static String url = "https://52toys.ik3cloud.com/k3cloud/Kingdee.K3.SCM.WebApi.ServicesStub.InventoryQueryService.GetInventoryData.common.kdsvc";
    private static String auh = "https://52toys.ik3cloud.com/k3cloud/Kingdee.BOS.WebApi.ServicesStub.AuthService.LoginByAppSecret.common.kdsvc";
    private static String orgs = "10000,10001,10002,10003,10004,10005,10006," +
            "20001,20002,20003,20004,20005,20006,20007,20008,20009,20010,20011,20012,20013,20014,20015,20016,20017,20018," +
            "30000,30001,50002";
    private static int pageRows = 10000;

    public static void main(String[] args) throws Exception {
        String testFlag = args[0];
        String dt = DateTime.now().toDateStr();

        // 配置
        Properties property = new Properties();
        InputStream resourceAsStream = StockCollect.class.getClassLoader().getResourceAsStream("kdwebapi.properties");
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "utf-8");
        property.load(inputStreamReader);

        // 下载路径
        String fileName = "stock-" + dt + "--" + DateTime.now().getTime() + ".json";
        String filePath = "/datadisk/stock/" + fileName;

        if ("0".equals(testFlag)) {
            fileName = "stock-" + dt + "--" + DateTime.now().getTime() + ".json";
            filePath = "D:\\test\\" + fileName;
        }

        // 获取cookies
        JSONObject auhJson = new JSONObject();
        auhJson.put("acctID", property.getProperty("X-KDApi-AcctID"));
        auhJson.put("username", property.getProperty("X-KDApi-UserName"));
        auhJson.put("appid", property.getProperty("X-KDApi-AppID"));
        auhJson.put("appsecret", property.getProperty("X-KDApi-AppSec"));
        auhJson.put("lcid", property.getProperty("X-KDApi-LCID"));

        HttpRequest postAuh = HttpUtil.createPost(auh);
        HttpRequest body = postAuh.body(auhJson.toString());
        HttpResponse response = body.execute();
        List<HttpCookie> cookies = response.getCookies();
        int flag = JSONObject.parse(response.body()).getIntValue("LoginResultType");
        if (flag != 1) {
            return;
        }

        // 获取仓库数据
        HttpRequest post;
        String json;
        int page = 0;
        JSONObject unit;
        HttpResponse stockReponse;
        String resultBody;
        JSONObject resultJson;
        JSONArray data;
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "utf-8"));
        while (true) {
            page++;
            json = "{\"parameters\":\"[{\\\"fstockorgnumbers\\\":\\\"" + orgs + "\\\",\\\"fmaterialnumbers\\\":\\\"\\\"," +
                    "\\\"fstocknumbers\\\":\\\"\\\",\\\"flotnumbers\\\":\\\"\\\",\\\"isshowstockloc\\\":true,\\\"isshowauxprop\\\":true," +
                    "\\\"pageindex\\\":" +
                    page + ",\\\"pagerows\\\":" + pageRows +
                    "}]\"}";
            post = HttpUtil.createPost(url);
            post.cookie(cookies);
            post.body(json);
            stockReponse = post.execute();
            resultBody = stockReponse.body();
            System.out.println(resultBody);
            resultJson = JSONObject.parse(resultBody);
            data = resultJson.getJSONArray("data");
            if (data == null || data.size() == 0) {
                break;
            }
            for (int i = 0; i < data.size(); i++) {
                unit = data.getJSONObject(i);
                System.out.println(unit);
                bufferedWriter.write(unit.toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }

        bufferedWriter.close();

        if ("0".equals(testFlag)) {
            return;
        }

        try {
            ProcessBuilder bash = new ProcessBuilder("bash", "/home/52toys/bin/stock.sh", dt, filePath, fileName);
            Process process = bash.start();
            int exitCode = process.waitFor();
            System.out.println(exitCode);
        } catch (Exception e) {
        }
    }
}
