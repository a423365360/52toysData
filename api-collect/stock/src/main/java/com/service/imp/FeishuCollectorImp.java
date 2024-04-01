package com.service.imp;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bean.FeiShuMaterial;
import com.service.FeishuCollector;
import com.util.ConnectUtil;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

public class FeishuCollectorImp implements FeishuCollector {
    private static String tokenURL = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
    private static String instanceIDURL = "https://open.feishu.cn/open-apis/approval/v4/instances";
    private static String instanceDetail = "https://open.feishu.cn/open-apis/approval/v4/instances/";
    private static String pendingQuery = "SELECT start_time FROM kingdee.dwd_feishu_stock WHERE status = 'PENDING'";

    HttpRequest getInstanceDetail, getInstanceID, postToken, postTokenRequest;
    String responseInstanceDetailBody, result, pageToken = "", token, instanceIdFinalURL, responseInstanceIDBody;
    JSONObject responseInstanceDetailBodyJson, jsonUnitSub, subAttribute, responseInstanceIDBodyJson, from, data, payload;
    JSONArray resultArray, materialList, materialPropertyList, jsonArray;
    boolean hasMore, reverted;
    long startTime, endTime;
    HttpResponse responseInstanceID;
    FeiShuMaterial bean;
    String departmentId, approvalCode, approvalName, openId, serialNumber, status, taskList, timeLine, userId, uuid, startTimestamp, fileName, filePath;
    long end_time, start_time;
    BufferedWriter bufferedWriter;
    ProcessBuilder bash;
    HashSet<String> createDaySet;
    Process process;

    @Override
    public void collect(String testFlag, String dt) throws Exception {
        this.end_time = DateTime.of(dt, "yyyy-MM-dd").offset(DateField.HOUR, 24).getTime();
        this.start_time = DateTime.of(dt, "yyyy-MM-dd").getTime();

        // 配置
        this.postToken = HttpUtil.createPost(tokenURL);
        this.postToken.header("Content-Type", "application/json; charset=utf-8");
        this.payload = new JSONObject();
        this.payload.put("app_id", "cli_a25e7be0f0f9900c");
        this.payload.put("app_secret", "lKSsGgB5MBQWIpca1THWNdo8wojegg7h");
        this.postTokenRequest = this.postToken.body(this.payload.toString());

        // 下载路径
        this.fileName = "feishu-" + dt + "--" + DateTime.now().getTime() + ".json";
        this.filePath = "/datadisk/stock/" + this.fileName;

        if ("0".equals(testFlag)) {
            this.fileName = "feishu-" + dt + "--" + DateTime.now().getTime() + ".json";
            this.filePath = "D:\\test\\" + this.fileName;
        }

        this.bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(this.filePath, true), "utf-8"));

        do {
            try {
                // 获取token
                this.token = JSONObject.parse(this.postTokenRequest.execute().body()).getString("tenant_access_token");

                // 获取实例id
                this.instanceIdFinalURL = instanceIDURL
                        + "?approval_code=276654E7-A1B0-43AA-B235-485222759FC9&start_time=" + start_time
                        + "&end_time=" + end_time
                        + "&page_token=" + pageToken;
                this.getInstanceID = HttpUtil.createGet(this.instanceIdFinalURL);
                this.getInstanceID.header("Authorization", "Bearer " + this.token);
                this.responseInstanceID = this.getInstanceID.execute();
                this.responseInstanceIDBody = this.responseInstanceID.body();
                this.responseInstanceIDBodyJson = JSONObject.parse(this.responseInstanceIDBody);
                this.jsonArray = this.responseInstanceIDBodyJson.getJSONObject("data").getJSONArray("instance_code_list");
                this.hasMore = this.responseInstanceIDBodyJson.getJSONObject("data").getBoolean("has_more");
                this.pageToken = this.responseInstanceIDBodyJson.getJSONObject("data").getString("page_token");

                // 获取详情
                for (Object instanceCode : jsonArray) {
                    this.getInstanceDetail = HttpUtil.createGet(instanceDetail + instanceCode);
                    this.getInstanceDetail.header("Authorization", "Bearer " + token);
                    this.getInstanceDetail.header("Content-Type", "application/json; charset=utf-8");
                    this.responseInstanceDetailBody = this.getInstanceDetail.execute().body();
                    this.responseInstanceDetailBodyJson = JSONObject.parse(this.responseInstanceDetailBody);
                    this.data = this.responseInstanceDetailBodyJson.getJSONObject("data");
                    this.result = this.data.getString("form");
                    this.approvalCode = this.data.getString("approval_code");
                    this.approvalName = this.data.getString("approval_name");
                    this.departmentId = this.data.getString("department_id");
                    this.openId = this.data.getString("open_id");
                    this.status = this.data.getString("status");
                    this.taskList = this.data.getString("task_list");
                    this.timeLine = this.data.getString("timeline");
                    this.reverted = this.data.getBoolean("reverted");
                    this.serialNumber = this.data.getString("serial_number");
                    this.userId = this.data.getString("user_id");
                    this.uuid = this.data.getString("uuid");
                    this.startTime = this.data.getLongValue("start_time");
                    this.endTime = this.data.getLongValue("end_time");
                    this.startTimestamp = DateTime.of(this.startTime).toTimestamp().toString();

                    this.resultArray = JSONArray.parse(result);
                    for (Object jsonUnit : this.resultArray) {
                        this.jsonUnitSub = JSONObject.parse(jsonUnit.toString());
                        if ("widget16541413657390001".equals(this.jsonUnitSub.getString("id"))) {
                            this.materialList = this.jsonUnitSub.getJSONArray("value");
                            for (Object material : this.materialList) {
                                this.bean = new FeiShuMaterial();
                                this.materialPropertyList = JSONArray.parse(material.toString());
                                for (Object property : this.materialPropertyList) {
                                    this.subAttribute = JSONObject.parse(property.toString());
                                    switch (this.subAttribute.getString("id")) {
                                        case "widget16541414877060001":
                                            this.bean.setMaterialName(this.subAttribute.getString("value"));
                                            continue;
                                        case "widget16566604998050001":
                                            this.bean.setMaterialNumber(this.subAttribute.getString("value"));
                                            continue;
                                        case "widget16554496169870001":
                                            this.bean.setWholeSale(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16554496213310001":
                                            this.bean.setBranch(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16554496229900001":
                                            this.bean.seteCommerce(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16621098396110001":
                                            this.bean.setOverseaCommerce(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16554496248270001":
                                            this.bean.setDanqu(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16621102243450001":
                                            this.bean.setTikTok(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16566594497560001":
                                            this.bean.setBuyForm(this.subAttribute.getString("value"));
                                            continue;
                                        case "widget16589060725580001":
                                            this.bean.setProjectID(this.subAttribute.getString("value"));
                                            continue;
                                        case "widget16566605022510001":
                                            this.bean.setProductLineCode(this.subAttribute.getString("value"));
                                            continue;
                                        case "widget16566562170640001":
                                            this.bean.setRefund(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16566562462730001":
                                            this.bean.setFree(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16566569369690001":
                                            this.bean.setProduceCost(this.subAttribute.getDouble("value"));
                                            continue;
                                        case "widget16566569907140001":
                                            this.bean.setCostPrice(this.subAttribute.getDouble("value"));
                                            continue;
                                        case "widget16639197161610001":
                                            this.bean.setProductSeries(this.subAttribute.getString("value"));
                                            continue;
                                        case "widget16566596034680001":
                                            this.bean.setProdcutType(this.subAttribute.getString("value"));
                                            continue;
                                        case "widget16566572394490001":
                                            this.bean.setPrice(this.subAttribute.getDouble("value"));
                                            continue;
                                        case "widget16554496637370001":
                                            this.bean.setTotalNumber(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16566559689470001":
                                            this.bean.setEtc1(this.subAttribute.getInteger("value"));
                                            continue;
                                        case "widget16566561164890001":
                                            this.bean.setEtc2(this.subAttribute.getInteger("value"));
                                    }
                                }
                                this.from = JSONObject.from(this.bean);
                                this.from.put("api_time", DateTime.now().toTimestamp());
                                this.from.put("startTime", startTime);
                                this.from.put("endTime", endTime);
                                this.from.put("approvalCode", approvalCode);
                                this.from.put("approvalName", approvalName);
                                this.from.put("departmentId", departmentId);
                                this.from.put("instanceCode", instanceCode.toString());
                                this.from.put("openId", openId);
                                this.from.put("status", status);
                                this.from.put("taskList", taskList);
                                this.from.put("timeLine", timeLine);
                                this.from.put("reverted", reverted);
                                this.from.put("serialNumber", serialNumber);
                                this.from.put("userId", userId);
                                this.from.put("uuid", uuid);
                                this.from.put("startTimestamp", startTimestamp);
                                this.bufferedWriter.write(this.from.toString()
                                        .replace("\\\\n", "")
                                        .replace("\\n", "")
                                        .replace("\\t", ""));
                                this.bufferedWriter.newLine();
                                this.bufferedWriter.flush();
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        } while (this.hasMore);
        this.bufferedWriter.close();

        if ("0".equals(testFlag)) {
            return;
        }
        try {
            this.bash = new ProcessBuilder("bash", "/home/52toys/bin/feishu.sh", dt, filePath, fileName);
            this.process = bash.start();
        } catch (Exception e) {
        }
    }

    @Override
    public void update(String testFlag) throws Exception {
        try (Connection hiveConnection = ConnectUtil.getHiveConnection("kingdee", testFlag);
             PreparedStatement pendingQueryPs = hiveConnection.prepareStatement(pendingQuery)) {
            ResultSet pendingResultSet = pendingQueryPs.executeQuery();
            this.createDaySet = new HashSet<>();
            while (pendingResultSet.next()) {
                long startTimeResult = pendingResultSet.getLong("start_time");
                this.createDaySet.add(DateTime.of(startTimeResult).toDateStr());
            }

            for (String dt : this.createDaySet) {
                System.out.println(dt);
                this.collect(testFlag, dt);
            }
        }
    }

    @Override
    public void history(String testFlag, String start, String end) {

    }
}
