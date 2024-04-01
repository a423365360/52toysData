package com.hive;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

public class GuanyiJson {
    public static void main(String[] args) throws Exception {
        String dt1 = args[0];
        String dt2 = args[1];

        DateTime date1 = DateTime.of(dt1, "yyyy-MM-dd");
        DateTime date2 = DateTime.of(dt2, "yyyy-MM-dd");
        DateTime date0 = date1;
        while (date0.before(date2)) {
            String dt = DateTime.of(date0).toDateStr();
            date0 = DateUtil.dateNew(date0).offset(DateField.HOUR, 24);

            List<CsvRow> rows = null;

            String inPath = "D:\\test\\gy\\2021\\" + dt + ".csv";
            String outPath = "D:\\test\\json\\2021\\" + dt + ".json";
//        String inPath = "/datadisk/gy/selenium/" + dt + ".csv";
//        String outPath = "/datadisk/gy/selenium/json/" + dt + ".json";

            try (CsvReader reader = CsvUtil.getReader()) {
                CsvData data = reader.read(FileUtil.file(inPath), Charset.forName("GB2312"));
                rows = data.getRows();
            } catch (Exception e) {
                System.out.println("CSV Error");
            }

            if (rows == null) {
                continue;
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath, true)))) {
                for (CsvRow csvRow : rows) {
                    if ("店铺类型".equals(csvRow.get(0))) {
                        continue; // 过滤字段
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("from_type_name", csvRow.get(0).replace("\t", ""));     // 店铺类型
                    jsonObject.put("shop_name", csvRow.get(1).replace("\t", ""));     // 店铺名称
                    jsonObject.put("code", csvRow.get(2).replace("\t", ""));    // 单据编号
                    jsonObject.put("order_type_name", csvRow.get(3));    // 订单类型
                    jsonObject.put("dealtime", csvRow.get(4));    // 拍单时间
                    jsonObject.put("paytime", csvRow.get(5));    // 付款时间
                    jsonObject.put("approveDate", csvRow.get(6));    // 审核时间
                    jsonObject.put("vip_code", csvRow.get(7).replace("\t", ""));    // 会员代码
                    jsonObject.put("vip_name", csvRow.get(8).replace("\t", ""));    // 会员名称
//                jsonObject.put("salesman", csvRow.get(9));
                    jsonObject.put("warehouse_name", csvRow.get(10).replace("\t", ""));    // 建议仓库
                    jsonObject.put("express_name", csvRow.get(11).replace("\t", ""));    // 建议快递
//            jsonObject.put("product_picture", csvRow.get(12));    // 商品图片
                    jsonObject.put("xproduct_item_code", csvRow.get(13).replace("\t", ""));    // 商品代码
                    jsonObject.put("xproduct_item_name", csvRow.get(14));    // 规格名称
                    jsonObject.put("xproduct_item_simple_name", csvRow.get(15));    // 商品简称
//            jsonObject.put("specification_code", csvRow.get(16));    // 规格代码
//            jsonObject.put("specification_name", csvRow.get(17));    // 规格名称
//            jsonObject.put("sku_note", csvRow.get(18));    // 商品备注
                    jsonObject.put("substitut_order", csvRow.get(19));    // 代发订单
                    jsonObject.put("plan_delivery_date", csvRow.get(20));    // 预计发货时间
                    jsonObject.put("xproduct_qty", csvRow.get(21));    // 订购数量
                    jsonObject.put("xproduct_weight", csvRow.get(22));    // 总重量
                    jsonObject.put("xproduct_discount", csvRow.get(23));    // 折扣
                    jsonObject.put("xproduct_origin_price", csvRow.get(24));    // 标准单价
                    jsonObject.put("xproduct_origin_amount", csvRow.get(25));    // 标准金额
                    jsonObject.put("xproduct_price", csvRow.get(26));    // 实际单价
                    jsonObject.put("xproduct_amount", csvRow.get(27));    // 实际金额
                    jsonObject.put("xproduct_amount_after", csvRow.get(28));    // 让利后金额
                    jsonObject.put("xproduct_discount_fee", csvRow.get(29));    // 让利金额
                    jsonObject.put("xproduct_post_fee", csvRow.get(30));    // 物流费用
                    jsonObject.put("xproduct_cost_price_total", csvRow.get(31));    // 成本总价
//            jsonObject.put("buyer_memo", csvRow.get(32));    // 买家备注
//            jsonObject.put("seller_memo", csvRow.get(33));    // 卖家备注
                    jsonObject.put("create_name", csvRow.get(34).replace("\t", ""));    // 制单人
//                jsonObject.put("product_real_profit", csvRow.get(35));    // TODO 商品实际利润
//                jsonObject.put("product_standard_profit", csvRow.get(36));    // TODO 商品标准利润
                    jsonObject.put("platform_flag", csvRow.get(37));    // 平台旗帜
                    jsonObject.put("xproduct_platform_item_name", csvRow.get(38).replace("\t", ""));    // 平台商品名称
                    jsonObject.put("xproduct_platform_sku_name", csvRow.get(39).replace("\t", ""));    // 平台规格名称
                    if ("是".equals(csvRow.get(40).replace("\t", ""))) {
                        jsonObject.put("xproduct_is_gift", true);
                    } else {
                        jsonObject.put("xproduct_is_gift", false);
                    }
//            jsonObject.put("free_source", csvRow.get(41));    // 赠品来源
                    jsonObject.put("xproduct_other_service_fee", csvRow.get(42));    // 其他服务
//            jsonObject.put("invoice_type", csvRow.get(43));    // 发票种类
//            jsonObject.put("invoice_head_type", csvRow.get(44));    // 发票抬头类型
//            jsonObject.put("invoice_class", csvRow.get(45));    // 发票类型
//            jsonObject.put("bank", csvRow.get(46));    // 开户行
//            jsonObject.put("account_number", csvRow.get(47));    // 账号
//            jsonObject.put("invoice_phone", csvRow.get(48));    // 发票电话
//            jsonObject.put("invoice_address", csvRow.get(49));    // 发票地址
//            jsonObject.put("vipEmail", csvRow.get(50));    // 会员邮箱
//            jsonObject.put("receive_email", csvRow.get(51));    // 收获邮箱
                    jsonObject.put("platform_code", csvRow.get(52).replace("\t", ""));    // 平台账号
//            jsonObject.put("invoince_head", csvRow.get(53));    // 发票抬头
//            jsonObject.put("invoince_content", csvRow.get(54));    // 发票内容
//            jsonObject.put("taxpayer_identification_number", csvRow.get(55));    // 纳税人识别号
                    jsonObject.put("receiver_name", csvRow.get(56).replace("\t", ""));    // 收货人
                    jsonObject.put("receiver_mobile", csvRow.get(57).replace("\t", ""));    // 收货人手机
                    jsonObject.put("receiver_zip", csvRow.get(58));    // 邮编
                    jsonObject.put("receiver_address", csvRow.get(59));    // 收货地址
                    // 是否退款
                    switch (csvRow.get(60).replace("\t", "")) {
                        case "未退款":
                            jsonObject.put("refund", 0);
                        case "退款成功":
                            jsonObject.put("refund", 1);
                        case "退款中":
                            jsonObject.put("refund", 2);
                    }
//            jsonObject.put("platform_product_id", csvRow.get(61));    // 平台商品ID
//            jsonObject.put("platform_model_id", csvRow.get(62));    // 平台规格ID
//            jsonObject.put("lot", csvRow.get(63));    // 波次号
//            jsonObject.put("host_information", csvRow.get(64));    // 主播信息
                    jsonObject.put("approve", true);    // 审核状态 csvRow.get(65)
//            jsonObject.put("lot_information", csvRow.get(66));    // 批次信息
//            jsonObject.put("total_volume", csvRow.get(67));    // 总体积
//            jsonObject.put("out_stock_bill", csvRow.get(68));    // 外仓单据
//            jsonObject.put("combine_item_name", csvRow.get(69));    // 组合商品名称
                    jsonObject.put("xproduct_plat_discount_amount", csvRow.get(70));    // 平台折扣金额
                    jsonObject.put("confirm_receipt_time", csvRow.get(71));    // 确认收货时间
                    jsonObject.put("modifytime", csvRow.get(72));    // 发货时间
                    jsonObject.put("createtime", dt + " 00:00:00");


                    jsonObject.put("cancle", false);    // TODO 管易api错误
                    jsonObject.put("xproduct_cancel", false);

                    jsonObject.put("api_timestamp", DateTime.now().toSqlDate());  // API_timestamp
                    bufferedWriter.write(jsonObject.toString());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            } catch (Exception e) {
                System.out.println("Write Error");
            }
        }
    }
}
