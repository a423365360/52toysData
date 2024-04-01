package com.hive;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;

import java.net.HttpCookie;
import java.util.List;

public class StockCollectExample {
    private static String url = "https://52toys.ik3cloud.com/k3cloud/Kingdee.K3.SCM.WebApi.ServicesStub.InventoryQueryService.GetInventoryData.common.kdsvc";
    private static String auh = "https://52toys.ik3cloud.com/k3cloud/Kingdee.BOS.WebApi.ServicesStub.AuthService.LoginByAppSecret.common.kdsvc";

    public static void main(String[] args) throws Exception {
        JSONObject auhJson = new JSONObject();
        auhJson.put("acctID", "账套ID");
        auhJson.put("username", "集成用户名");
        auhJson.put("appid", "授权的应用ID");
        auhJson.put("appsecret", "应用密钥");
        auhJson.put("lcid", "2052");

        // 获取cookie
        HttpRequest postAuh = HttpUtil.createPost(auh);
        HttpRequest body = postAuh.body(auhJson.toString());
        HttpResponse response = body.execute();
        List<HttpCookie> cookies = response.getCookies();

        // 获取仓库数据
        String json = "{\"parameters\":\"[{\\\"fstockorgnumbers\\\":\\\"10000\\\",\\\"fmaterialnumbers\\\":\\\"\\\"," +
                "\\\"fstocknumbers\\\":\\\"\\\",\\\"flotnumbers\\\":\\\"\\\",\\\"isshowstockloc\\\":true,\\\"isshowauxprop\\\":true," +
                "\\\"pageindex\\\":1,\\\"pagerows\\\":1000}]\"}";
        HttpRequest post = HttpUtil.createPost(url);
        post.cookie(cookies);
        post.body(json);
        HttpResponse stockReponse = post.execute();
        System.out.println(stockReponse);
    }
}
