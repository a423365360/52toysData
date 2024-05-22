package com.excel.imp2;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.excel.ExcelSheetBI;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AdsVs14 implements ExcelSheetBI {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;
    static int startOffset = 3;

    public AdsVs14(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql1, String sql2, String dt) throws Exception {
        // TODO MOSS
        String mossSql = "select * from ads_vs_14_550 where dt ='" + dt + "'";

        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        CellStyle midStyle = xssfWorkbook.createCellStyle();
        midStyle.setAlignment(HorizontalAlignment.CENTER);
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));
        SXSSFSheet sheetG;

        // 新品对标产品

        try (PreparedStatement ps1 = hiveConnection.prepareStatement(sql1);
             PreparedStatement ps2 = hiveConnection.prepareStatement(sql2);
             PreparedStatement ps3 = hiveConnection.prepareStatement(mossSql)) {

            HashMap<String, Integer> fisrtDayNumberMap = new HashMap<>();
            ResultSet firstDayResultSet = ps2.executeQuery();
            while (firstDayResultSet.next()) {
                fisrtDayNumberMap.put(firstDayResultSet.getString("product_series"), firstDayResultSet.getInt("total_number"));
            }
            SXSSFSheet sheet6 = xssfWorkbook.createSheet(table);
            ResultSet resultSet5 = ps1.executeQuery();
            ResultSet resultSetMoss = ps3.executeQuery();
            int count6 = 0, sameFlag1 = -1, sameFlag2 = -1, sameGraphFlag1 = -1, sum, firstDayNumber, productLineCount = 0, sefSumSum = 0;
            int[] selfSum = new int[14];
            int[] selfMoss = new int[14];
            ArrayList<SXSSFRow> rows = new ArrayList<>();
            HashSet<Integer> rk1List = new HashSet<>();
            ArrayList<Integer> days;
            String productLineFlag = "", group = "";
            SXSSFRow row61, row62, rowSheet6, rowG0, rowProductSeries = null;

            // Sheet名称
            sheet6.createRow(0).createCell(0).setCellValue(table);
            for (int i = 0; i < 14; i++) {
                selfMoss[i] = 0;
            }

            while (resultSet5.next()) {
                int rk1 = resultSet5.getInt("rk1");
                int rk2 = resultSet5.getInt("rk2");
                int id = resultSet5.getInt("id");
                int onoff = resultSet5.getInt("onoff");
                String productLine = resultSet5.getString("product_line");
                String productSeries = resultSet5.getString("product_series");
                String businessLine = resultSet5.getString("business_line");
                Date saleDate = resultSet5.getDate("sale_date");
                days = new ArrayList<>();

                // 发售最大天数
                int max = (int) DateUtil.betweenDay(saleDate, DateTime.of(dt, "yyyy-MM-dd"), true) + 1;

                // 判断是否发售
                if (DateUtil.compare(DateTime.of(dt, "yyyy-MM-dd"), saleDate) < 0) {
                    max = max * (-1);
                }

                // 未发售
                if (max < 1) {
                    rk1List.add(rk1);
                    if (sameFlag1 != rk1 || sameFlag2 != rk2) {
                        if (productLineFlag.equals(productLine)) {
                            if (sameFlag1 != rk1) {
                                productLineCount++;
                            }
                        } else {
                            productLineCount = 1;
                        }
                        productLineFlag = productLine;
                        sameFlag1 = rk1;
                        sameFlag2 = rk2;
                        count6 += 2;
                        row61 = sheet6.createRow(count6);
//                    row61.createCell(0).setCellValue(rk1);
                        group = (rk2 == 1 ? productLine + "-新品" + productLineCount : productLine + "对标产品" + productLineCount);
                        row61.createCell(1).setCellValue(group);
                        row61.createCell(2).setCellValue(productSeries);
                        continue;
                    } else {
                        continue;
                    }
                }

                // 载入数据
                int maxDays = Math.min(14, max);
                for (int i = 1; i <= maxDays; i++) {
                    days.add(resultSet5.getInt("day" + i));
                }


                // TODO  抖音直播之前插入京东和拼多多
                if (productSeries.equals("万能匣系列《流浪地球2》-550系列智能量子计算机") && businessLine.equals("直播-抖音店铺")) {

                    for (int i = 0; i < 2; i++) {
                        resultSetMoss.next();

                        SXSSFRow mossRoss = sheet6.createRow(count6);
                        mossRoss.createCell(1).setCellValue(group);
                        mossRoss.createCell(2).setCellValue(resultSetMoss.getString("business_line"));

                        // TODO 京东拼多多业务线合计
                        int mossSum = 0;
                        for (int j = 0; j < maxDays; j++) {
                            mossRoss.createCell(j + startOffset).setCellValue(resultSetMoss.getInt("day" + (j + 1)));
                            mossSum += resultSetMoss.getInt("day" + (j + 1));

                            // 每日自营合计
                            selfMoss[j] = selfMoss[j] + resultSetMoss.getInt("day" + (j + 1));
                        }

                        // 合计
                        mossRoss.createCell(maxDays + startOffset).setCellValue(mossSum);
                        count6++;
                    }
                }

                // 新增Block 表头
                if (sameFlag1 != rk1 || sameFlag2 != rk2) {
                    // 清空自营合计
                    for (int i = 0; i < 14; i++) {
                        selfSum[i] = 0;
                    }

                    // 判断产品线分组
                    if (productLineFlag.equals(productLine)) {
                        if (sameFlag1 != rk1) {
                            productLineCount++;
                        }
                    } else {
                        productLineCount = 1;
                    }
                    productLineFlag = productLine;
                    group = (rk2 == 1 ? productLine + "-新品" + productLineCount : productLine + "-对标产品" + productLineCount);
                    sameFlag1 = rk1;
                    sameFlag2 = rk2;
                    count6 += 2;
                    row61 = sheet6.createRow(count6);
                    row61.createCell(2).setCellValue(productSeries);

                    // 中文星期
                    for (int columnNumber = startOffset; columnNumber < maxDays + startOffset; columnNumber++) {
                        row61.createCell(columnNumber).setCellValue(
                                DateUtil.dateNew(saleDate).offset(DateField.HOUR, 24 * (columnNumber - 3)).dayOfWeekEnum().toChinese());
                    }

                    count6++;
                    row62 = sheet6.createRow(count6);
//                row62.createCell(0).setCellValue("产品分组");
                    row62.createCell(1).setCellValue("分组");
                    row62.createCell(2).setCellValue("业务平台");

                    // Day表头
                    for (int i = 1; i <= maxDays; i++) {
                        row62.createCell((i - 1) + startOffset).setCellValue("Day" + i);
                    }
                    row62.createCell(maxDays + startOffset).setCellValue("合计");
                    count6++;
                }

                // 批发前插入自营合计
                if ("批发".equals(businessLine)) {
                    // 清空自营合计的合计
                    sefSumSum = 0;
                    rowSheet6 = sheet6.createRow(count6);
                    count6++;
                    rowSheet6.createCell(1).setCellValue(group);
//                rowSheet6.createCell(2).setCellValue("自营-合计");

                    SXSSFCell cell = rowSheet6.createCell(2);
                    cell.setCellStyle(midStyle);
                    cell.setCellValue("自营-合计");
                    for (int i = 0; i < maxDays; i++) {

                        // TODO 合并京东拼多多
                        rowSheet6.createCell(i + startOffset).setCellValue(selfSum[i] + selfMoss[i]);
                        sefSumSum += (selfSum[i] + selfMoss[i]);
                    }
                    rowSheet6.createCell(maxDays + startOffset).setCellValue(sefSumSum);
                }
                rowSheet6 = sheet6.createRow(count6);
//            rowSheet6.createCell(0).setCellValue(rk1);
                rowSheet6.createCell(1).setCellValue(group);
                rowSheet6.createCell(2).setCellValue(businessLine);

                // 输出数据
                sum = 0;
                for (int i = 0; i < maxDays; i++) {
                    int dayNumber = days.get(i);

                    // 批发首日替换为预售数据
                    if ("批发".equals(businessLine) && i == 0) {
                        try {
                            dayNumber = fisrtDayNumberMap.get(productSeries);
                        } catch (Exception e) {
                        }
                    }
                    rowSheet6.createCell(i + startOffset).setCellValue(dayNumber);

                    // 自营-合计
                    if (!("批发".equals(businessLine))) {
                        selfSum[i] = selfSum[i] + dayNumber;
                    }

                    // 业务线合计
                    sum += dayNumber;
                }
                rowSheet6.createCell(maxDays + startOffset).setCellValue(sum);
                count6++;

                // 筛选id 排除批发  分sheet展示
                if (id > 5 || onoff == 0) {
                    continue;
                }

                if (rk1List.contains(rk1)) {
                    continue;
                }

                if (sameGraphFlag1 != rk1) {
                    sameGraphFlag1 = rk1;
                    sheetG = xssfWorkbook.createSheet(productSeries);
                    rowG0 = sheetG.createRow(0);
                    rowG0.createCell(1).setCellValue("52旗舰店");
                    rowG0.createCell(2).setCellValue("52旗舰店");
                    rowG0.createCell(3).setCellValue("直播");
                    rowG0.createCell(4).setCellValue("直播");
                    rowG0.createCell(5).setCellValue("小红书");
                    rowG0.createCell(6).setCellValue("小红书");
                    rowG0.createCell(7).setCellValue("蛋趣");
                    rowG0.createCell(8).setCellValue("蛋趣");
                    rowG0.createCell(9).setCellValue("门店");
                    rowG0.createCell(10).setCellValue("门店");
                    rowProductSeries = sheetG.createRow(1);
                    rows.clear();
                    for (int i = 0; i < 14 * 5; i++) {
                        rows.add(sheetG.createRow(i + 2));
                        rows.get(i).createCell(0).setCellValue(i % 14 + 1);
                    }
                }

                if (rk2 == 2) {
                    for (int i = 1; i <= 5; i++) {
                        rowProductSeries.createCell(2 * i).setCellValue(productSeries);
                    }
                } else if (rk2 == 1) {
                    for (int i = 1; i <= 5; i++) {
                        rowProductSeries.createCell(2 * i - 1).setCellValue(productSeries);
                    }
                }

                for (int i = 0; i < maxDays; i++) {
                    rows.get((id - 1) * 14 + i).createCell(rk2 + (id - 1) * 2).setCellValue(days.get(i));
                }
            }
        } catch (Exception e) {
        }
    }
}
