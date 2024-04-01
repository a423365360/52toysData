package com.util;


import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpUtil {
    private static Logger logger = Logger.getLogger(Object.class);

    public static int getTotalNumber(String url, String data) throws Exception {
        HttpPost httppost = new HttpPost(url);
        StringEntity stringentity = new StringEntity(URLEncoder.encode(data, "utf-8"), ContentType.create("text/json", "UTF-8"));
        httppost.setEntity(stringentity);
        try (CloseableHttpClient httpclient = HttpClients.createDefault();
             CloseableHttpResponse httpresponse = httpclient.execute(httppost)) {
            String response = EntityUtils.toString(httpresponse.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(response);
            return jsonObject.getIntValue("total");
        } catch (Exception var11) {
            var11.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<String> getBillNumberList(String url, String data) throws Exception {
        ArrayList<String> billNumberList = new ArrayList<>();
        HttpPost httppost = new HttpPost(url);
        StringEntity stringentity = new StringEntity(URLEncoder.encode(data, "utf-8"), ContentType.create("text/json", "UTF-8"));
        httppost.setEntity(stringentity);
        try (CloseableHttpClient httpclient = HttpClients.createDefault();
             CloseableHttpResponse httpresponse = httpclient.execute(httppost)) {
            String response = EntityUtils.toString(httpresponse.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(response);
            JSONArray orders = jsonObject.getJSONArray("orders");
            for (int i = 0; i < orders.size(); i++) {
                JSONObject order = orders.getJSONObject(i);
                String billNumber = order.getString("code");
                billNumberList.add(billNumber);
            }
        } catch (Exception e) {
        }
        return billNumberList;
    }

    public static void getGuanyiData(String url, String data, String filePath) throws Exception {
        HttpPost httppost = new HttpPost(url);
        StringEntity stringentity = new StringEntity(URLEncoder.encode(data, "utf-8"), ContentType.create("text/json", "UTF-8"));
        httppost.setEntity(stringentity);
        try (CloseableHttpClient httpclient = HttpClients.createDefault();
             CloseableHttpResponse httpresponse = httpclient.execute(httppost);
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "utf-8"))) {
            String response = EntityUtils.toString(httpresponse.getEntity());
            JSONObject resultJson = JSONObject.parseObject(response);
            JSONObject orderDetail = resultJson.getJSONObject("orderDetail");
            JSONArray details = orderDetail.getJSONArray("details");
            if (details == null) {
                System.out.println("no details");
                bufferedWriter.write(orderDetail.toString());
                bufferedWriter.flush();
                bufferedWriter.newLine();
                return;
            }
            for (int i = 0; i < details.size(); i++) {
                try {
                    JSONObject product = details.getJSONObject(i);
                    JSONObject orderDetailNew = resultJson.getJSONObject("orderDetail");
                    orderDetailNew.put("xproduct_oid", product.getString("oid"));      // 子订单号
                    orderDetailNew.put("xproduct_qty", product.getIntValue("qty"));    //  数量
                    orderDetailNew.put("xproduct_price", product.getDouble("price")); // 实际单价
                    orderDetailNew.put("xproduct_amount", product.getDouble("amount"));  // 实际金额
                    orderDetailNew.put("xproduct_refund", product.getIntValue("refund")); // 退款状态  0:未退款 1:退款成功 2:退款中
                    orderDetailNew.put("xproduct_discount", product.getDouble("discount"));
                    orderDetailNew.put("xproduct_note", product.getString("note"));
                    orderDetailNew.put("xproduct_cancel", product.getBoolean("cancel"));  // 是否取消
                    orderDetailNew.put("xproduct_del", product.getBoolean("del"));
                    orderDetailNew.put("xproduct_weight", product.getDouble("weight"));    // 标准重量
                    orderDetailNew.put("xproduct_item_code", product.getString("item_code"));  // 商品代码
                    orderDetailNew.put("xproduct_item_name", product.getString("item_name"));  // 商品名称
                    orderDetailNew.put("xproduct_item_simple_name", product.getString("item_simple_name")); //商品简称
                    orderDetailNew.put("xproduct_sku_name", product.getString("sku_name"));  // 规格名称
                    orderDetailNew.put("xproduct_sku_code", product.getString("sku_code"));  // 规格代码
                    orderDetailNew.put("xproduct_post_fee", product.getDouble("post_fee"));  // 物流费用
                    orderDetailNew.put("xproduct_discount_fee", product.getString("discount_fee"));  // 让利金额
                    orderDetailNew.put("xproduct_amount_after", product.getString("amount_after"));  // 让利后金额
                    orderDetailNew.put("xproduct_origin_price", product.getDouble("origin_price"));  // 标准单价
                    orderDetailNew.put("xproduct_origin_amount", product.getDouble("origin_amount")); // 标准金额
                    orderDetailNew.put("xproduct_platform_item_name", product.getString("platform_item_name")); // 平台商品名称
                    orderDetailNew.put("xproduct_platform_sku_name", product.getString("platform_sku_name"));   // 平台规格名称
                    orderDetailNew.put("xproduct_delivering_qty", product.getDouble("delivering_qty"));     // 配货数量
                    orderDetailNew.put("xproduct_delivered_qty", product.getDouble("delivered_qty"));       // 发货数量
                    orderDetailNew.put("xproduct_tax_rate", product.getDouble("tax_rate"));
                    orderDetailNew.put("xproduct_tax_amount", product.getDouble("tax_amount"));
                    orderDetailNew.put("xproduct_sku_note", product.getString("sku_note"));                      // 商品备注
                    orderDetailNew.put("xproduct_other_service_fee", product.getDouble("other_service_fee"));   //其他服务费
                    orderDetailNew.put("xproduct_is_gift", product.getBoolean("is_gift"));                         //  是否赠品
                    orderDetailNew.put("xproduct_tariff_amount", product.getDouble("tariff_amount"));
                    orderDetailNew.put("xproduct_item_unit_name", product.getString("item_unit_name"));
                    orderDetailNew.put("xproduct_tax_no", product.getString("tax_no"));
                    orderDetailNew.put("xproduct_exchange_rate", product.getDouble("exchange_rate"));
                    orderDetailNew.put("xproduct_cost_price", product.getDouble("cost_price"));                // 成本价
                    orderDetailNew.put("xproduct_cost_price_total", product.getDouble("cost_price_total"));    // 成本总价
                    orderDetailNew.put("xproduct_plat_discount_amount", product.getDouble("plat_discount_amount")); // 平台折扣金额
                    orderDetailNew.put("xproduct_distribution_post_fee", product.getDouble("distribution_post_fee"));
                    orderDetailNew.put("xproduct_gift_source_view", product.getString("gift_source_view"));         // 赠品来源
                    orderDetailNew.put("xproduct_saleable_qty", product.getDouble("saleable_qty"));             // 可销售数
                    orderDetailNew.put("xproduct_pickable_qty", product.getDouble("pickable_qty"));             // 可配货数
                    orderDetailNew.put("xproduct_warehouse_name", product.getString("warehouse_name"));
                    orderDetailNew.put("xproduct_stock_status_name", product.getString("stock_status_name"));
                    orderDetailNew.put("xproduct_estimate_arrived_date", product.getDate("estimate_arrived_date"));
                    orderDetailNew.put("xproduct_plan_delivery_date", product.getDate("plan_delivery_date"));
                    orderDetailNew.put("xproduct_maintain_num", product.getString("maintain_num"));
                    orderDetailNew.put("xproduct_assign_state", product.getString("assign_state"));
                    orderDetailNew.put("xproduct_delivery_state", product.getString("delivery_state"));
                    orderDetailNew.put("xproduct_pre_sale", product.getString("pre_sale"));
                    orderDetailNew.put("xproduct_warehouse_code", product.getString("warehouse_code"));
                    orderDetailNew.put("xproduct_store_code", product.getString("store_code"));
                    orderDetailNew.put("xproduct_bms_name", product.getString("bms_name"));
                    orderDetailNew.put("xproduct_minus_stock", product.getBoolean("minus_stock"));             // 允许负库存
                    orderDetailNew.put("xproduct_bms_status", product.getIntValue("bms_status"));               // bms标记
                    orderDetailNew.put("xproduct_pay_discount", product.getDouble("pay_discount"));
                    orderDetailNew.put("xproduct_combine_item_code", product.getString("combine_item_code"));  // 组合商品代码
                    orderDetailNew.put("xproduct_combine_item_name", product.getString("combine_item_name"));  // 组合商品名称
                    orderDetailNew.put("xproduct_drp_customer_name", product.getString("drp_customer_name"));  //
                    orderDetailNew.put("xproduct_combine_item_code_split", product.getString("combine_item_code_split"));  // 组合商品代码
                    orderDetailNew.put("xproduct_combine_item_name_split", product.getString("combine_item_name_split"));  // 组合商品名称
                    // TODO 覆盖details
                    orderDetailNew.put("details", "");  // 组合商品名称
                    bufferedWriter.write(orderDetailNew.toString());
                    bufferedWriter.flush();
                    bufferedWriter.newLine();
                } catch (Exception e) {
                    System.out.println("Error" + i);
                }
            }
        } catch (Exception e) {
        }
    }

    public static void sendPost(String url, String data) throws Exception {
        logger.info("url: " + url);
        logger.info("request: " + data);
        HttpPost httppost = new HttpPost(url);
        StringEntity stringentity = new StringEntity(URLEncoder.encode(data, "utf-8"), ContentType.create("text/json", "UTF-8"));
        httppost.setEntity(stringentity);
        String filePath = "D:\\test\\gy.json";

        try (CloseableHttpClient httpclient = HttpClients.createDefault();
             CloseableHttpResponse httpresponse = httpclient.execute(httppost);
             BufferedWriter bufferedWriter = new BufferedWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(filePath, true), "utf-8"))) {
            String response = EntityUtils.toString(httpresponse.getEntity());
//            bufferedWriter.write(response);
//            bufferedWriter.flush();
//            bufferedWriter.newLine();
            logger.info(response);
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }


    public static void sendPost(String url, String appkey, String sessionkey, String secret, String version, String method, String data) {
        logger.info("data: " + data);
        logger.info("url: " + url);
        StringBuffer requestContent = new StringBuffer();
        requestContent.append("appkey=");
        requestContent.append(appkey);
        requestContent.append("&sessionkey=");
        requestContent.append(sessionkey);
        requestContent.append("&sign=");
        requestContent.append(SignUtil.sign(data, secret));
        requestContent.append("&version=");
        requestContent.append(version);
        requestContent.append("&method=");
        requestContent.append(method);
        requestContent.append("&data=");

        try {
            requestContent.append(URLEncoder.encode(data, "UTF-8"));
        } catch (UnsupportedEncodingException var17) {
            var17.printStackTrace();
        }

        logger.info("request: " + requestContent.toString());

        try {
            CloseableHttpClient httpclient = null;
            CloseableHttpResponse httpresponse = null;

            try {
                httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(url);
                StringEntity stringentity = new StringEntity(requestContent.toString(), ContentType.create("text/plain", "UTF-8"));
                httppost.setEntity(stringentity);
                httpresponse = httpclient.execute(httppost);
                String response = EntityUtils.toString(httpresponse.getEntity());
                logger.info("response: " + response);
            } finally {
                if (httpclient != null) {
                    httpclient.close();
                }

                if (httpresponse != null) {
                    httpresponse.close();
                }

            }
        } catch (Exception var19) {
            var19.printStackTrace();
        }

    }
}
