package com.hive;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bean.FeiShuMaterial;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FeiShuHistory {
    private static String tokenURL = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
    private static String instanceIDURL = "https://open.feishu.cn/open-apis/approval/v4/instances";
    private static String instanceDetail = "https://open.feishu.cn/open-apis/approval/v4/instances/";

    public static void main(String[] args) throws IOException {
        HttpRequest getInstanceDetail, getInstanceID;
        String responseInstanceDetailBody, result, pageToken = "", token, instanceIdFinalURL, responseInstanceIDBody;
        JSONObject responseInstanceDetailBodyJson, jsonUnitSub, subAttribute, responseInstanceIDBodyJson, from, data;
        JSONArray resultArray, materialList, materialPropertyList, jsonArray;
        boolean hasMore, reverted;
        long startTime, endTime;
        HttpResponse responseInstanceID;
        FeiShuMaterial bean = null;
        String departmentId, approvalCode, approvalName, openId, serialNumber, status, taskList, timeLine, userId, uuid, startTimestamp;
        String fileName, filePath, dt;

        // 配置
        HttpRequest postToken = HttpUtil.createPost(tokenURL);
        postToken.header("Content-Type", "application/json; charset=utf-8");
        JSONObject payload = new JSONObject();
        payload.put("app_id", "cli_a25e7be0f0f9900c");
        payload.put("app_secret", "lKSsGgB5MBQWIpca1THWNdo8wojegg7h");
        HttpRequest postTokenRequest = postToken.body(payload.toString());

        long end_time = DateTime.of("2023-11-21", "yyyy-MM-dd").getTime();
        long start_time = DateTime.of("2022-10-01", "yyyy-MM-dd").getTime();

        do {
            // 获取token
            token = JSONObject.parse(postTokenRequest.execute().body()).getString("tenant_access_token");

            // 获取实例id
            instanceIdFinalURL = instanceIDURL
                    + "?approval_code=276654E7-A1B0-43AA-B235-485222759FC9&start_time=" + start_time
                    + "&end_time=" + end_time
                    + "&page_token=" + pageToken;
            getInstanceID = HttpUtil.createGet(instanceIdFinalURL);
            getInstanceID.header("Authorization", "Bearer " + token);
            responseInstanceID = getInstanceID.execute();
            responseInstanceIDBody = responseInstanceID.body();
            responseInstanceIDBodyJson = JSONObject.parse(responseInstanceIDBody);
            jsonArray = responseInstanceIDBodyJson.getJSONObject("data").getJSONArray("instance_code_list");
            hasMore = responseInstanceIDBodyJson.getJSONObject("data").getBoolean("has_more");
            pageToken = responseInstanceIDBodyJson.getJSONObject("data").getString("page_token");

            // 获取详情
            for (Object instanceCode : jsonArray) {
                getInstanceDetail = HttpUtil.createGet(instanceDetail + instanceCode);
                getInstanceDetail.header("Authorization", "Bearer " + token);
                getInstanceDetail.header("Content-Type", "application/json; charset=utf-8");
                responseInstanceDetailBody = getInstanceDetail.execute().body();
                responseInstanceDetailBodyJson = JSONObject.parse(responseInstanceDetailBody);
                data = responseInstanceDetailBodyJson.getJSONObject("data");
                result = data.getString("form");
                approvalCode = data.getString("approval_code");
                approvalName = data.getString("approval_name");
                departmentId = data.getString("department_id");
                openId = data.getString("open_id");
                status = data.getString("status");
                taskList = data.getString("task_list");
                timeLine = data.getString("timeline");
                reverted = data.getBoolean("reverted");
                serialNumber = data.getString("serial_number");
                userId = data.getString("user_id");
                uuid = data.getString("uuid");
                startTime = data.getLongValue("start_time");
                endTime = data.getLongValue("end_time");
                startTimestamp = DateTime.of(startTime).toTimestamp().toString();
                resultArray = JSONArray.parse(result);

                dt = DateTime.of(startTime).toDateStr();
                fileName = "feishu-" + dt + ".json";
                filePath = "D:\\test\\feishu\\" + fileName;

                BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(filePath, true), "utf-8"));

                for (Object jsonUnit : resultArray) {
                    jsonUnitSub = JSONObject.parse(jsonUnit.toString());
                    if ("widget16541413657390001".equals(jsonUnitSub.getString("id"))) {
                        materialList = jsonUnitSub.getJSONArray("value");
                        for (Object material : materialList) {
                            bean = new FeiShuMaterial();
                            materialPropertyList = JSONArray.parse(material.toString());
                            for (Object property : materialPropertyList) {
                                subAttribute = JSONObject.parse(property.toString());
                                switch (subAttribute.getString("id")) {
                                    case "widget16541414877060001":
                                        bean.setMaterialName(subAttribute.getString("value"));
                                        continue;
                                    case "widget16566604998050001":
                                        bean.setMaterialNumber(subAttribute.getString("value"));
                                        continue;
                                    case "widget16554496169870001":
                                        bean.setWholeSale(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16554496213310001":
                                        bean.setBranch(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16554496229900001":
                                        bean.seteCommerce(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16621098396110001":
                                        bean.setOverseaCommerce(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16554496248270001":
                                        bean.setDanqu(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16621102243450001":
                                        bean.setTikTok(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16566594497560001":
                                        bean.setBuyForm(subAttribute.getString("value"));
                                        continue;
                                    case "widget16589060725580001":
                                        bean.setProjectID(subAttribute.getString("value"));
                                        continue;
                                    case "widget16566605022510001":
                                        bean.setProductLineCode(subAttribute.getString("value"));
                                        continue;
                                    case "widget16566562170640001":
                                        bean.setRefund(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16566562462730001":
                                        bean.setFree(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16566569369690001":
                                        bean.setProduceCost(subAttribute.getDouble("value"));
                                        continue;
                                    case "widget16566569907140001":
                                        bean.setCostPrice(subAttribute.getDouble("value"));
                                        continue;
                                    case "widget16639197161610001":
                                        bean.setProductSeries(subAttribute.getString("value"));
                                        continue;
                                    case "widget16566596034680001":
                                        bean.setProdcutType(subAttribute.getString("value"));
                                        continue;
                                    case "widget16566572394490001":
                                        bean.setPrice(subAttribute.getDouble("value"));
                                        continue;
                                    case "widget16554496637370001":
                                        bean.setTotalNumber(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16566559689470001":
                                        bean.setEtc1(subAttribute.getInteger("value"));
                                        continue;
                                    case "widget16566561164890001":
                                        bean.setEtc2(subAttribute.getInteger("value"));
                                }
                            }
                            from = JSONObject.from(bean);
                            from.put("api_time", DateTime.now().toTimestamp());
                            from.put("startTime", startTime);
                            from.put("endTime", endTime);
                            from.put("approvalCode", approvalCode);
                            from.put("approvalName", approvalName);
                            from.put("departmentId", departmentId);
                            from.put("instanceCode", instanceCode.toString());
                            from.put("openId", openId);
                            from.put("status", status);
                            from.put("taskList", taskList);
                            from.put("timeLine", timeLine);
                            from.put("reverted", reverted);
                            from.put("serialNumber", serialNumber);
                            from.put("userId", userId);
                            from.put("uuid", uuid);
                            from.put("startTimestamp", startTimestamp);
                            System.out.println(from);
                            bufferedWriter.write(from.toString().replace("\\n", "").replace("\\t", ""));
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }
                }
                bufferedWriter.close();
            }
        } while (hasMore);
    }
}
