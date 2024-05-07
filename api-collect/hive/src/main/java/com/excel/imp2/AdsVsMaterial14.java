package com.excel.imp2;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.excel.ExcelSheetBI;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
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

public class AdsVsMaterial14 implements ExcelSheetBI {
    SXSSFWorkbook xssfWorkbook;
    Connection hiveConnection;

    public AdsVsMaterial14(SXSSFWorkbook xssfWorkbook, Connection hiveConnection) {
        this.xssfWorkbook = xssfWorkbook;
        this.hiveConnection = hiveConnection;
    }

    @Override
    public void setSheet(String table, String sql1, String sql2, String dt) throws Exception {
        CellStyle dateCellStyle = xssfWorkbook.createCellStyle();
        DataFormat dataFormat = xssfWorkbook.createDataFormat();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy/m/d"));

        // 新品对标产品(物料)
        int sameFlag1 = -1, sameFlag2 = -1;

        try (PreparedStatement ps1 = hiveConnection.prepareStatement(sql1);
             PreparedStatement ps2 = hiveConnection.prepareStatement(sql2)) {

            HashMap<String, Integer> fisrtDayNumberMaterialMap = new HashMap<>();
            ResultSet firstDayMaterialResultSet = ps2.executeQuery();
            while (firstDayMaterialResultSet.next()) {
                fisrtDayNumberMaterialMap.put(firstDayMaterialResultSet.getString("material_name"),
                        firstDayMaterialResultSet.getInt("total_number"));
            }
            SXSSFSheet sheet8 = xssfWorkbook.createSheet(table);
            ResultSet resultSet8 = ps1.executeQuery();
            int sameFlagMaterial1 = -1, sameFlagMaterial2 = -1, count8 = 0, sum, firstDayNumber;
            HashSet<Integer> rk1MateialList = new HashSet<>();
            ArrayList<Integer> daysMaterial;
            SXSSFRow row81, row82, rowSheet8;

            // Sheet名称
            sheet8.createRow(0).createCell(0).setCellValue(table);

            while (resultSet8.next()) {
                int rk1 = resultSet8.getInt("rk1");
                int rk2 = resultSet8.getInt("rk2");
                String productSeries = resultSet8.getString("product_series");
                String materialName = resultSet8.getString("material_name");
                String businessLine = resultSet8.getString("business_line");
                Date saleDate = resultSet8.getDate("sale_date");
                daysMaterial = new ArrayList<>();

                int max = (int) DateUtil.betweenDay(saleDate, DateTime.of(dt, "yyyy-MM-dd"), true) + 1;

                if (DateUtil.compare(DateTime.of(dt, "yyyy-MM-dd"), saleDate) < 0) {
                    max = max * (-1);
                }

                // 未发售
                if (max < 1) {
                    rk1MateialList.add(rk1);
                    if (sameFlagMaterial1 != rk1 || sameFlagMaterial2 != rk2) {
                        sameFlagMaterial1 = rk1;
                        sameFlagMaterial2 = rk2;
                        count8 += 2;
                        row81 = sheet8.createRow(count8);
                        row81.createCell(0).setCellValue(rk1);
                        row81.createCell(1).setCellValue(rk2 == 1 ? "新品" : "对标产品");
                        row81.createCell(2).setCellValue(productSeries);
                        continue;
                    } else {
                        continue;
                    }
                }

                // 可展示最大时间范围
                int maxDays = Math.min(14, max);

                for (int i = 1; i <= maxDays; i++) {
                    daysMaterial.add(resultSet8.getInt("day" + i));
                }

                if (sameFlag1 != rk1 || sameFlag2 != rk2) {
                    sameFlag1 = rk1;
                    sameFlag2 = rk2;
                    count8 += 2;
                    row81 = sheet8.createRow(count8);
                    row81.createCell(3).setCellValue(productSeries);

                    // 中文日期
                    for (int columnNumber = 4; columnNumber < maxDays + 4; columnNumber++) {
                        row81.createCell(columnNumber).setCellValue(
                                DateUtil.dateNew(saleDate).offset(DateField.HOUR, 24 * (columnNumber - 3)).dayOfWeekEnum().toChinese());
                    }

                    count8++;
                    row82 = sheet8.createRow(count8);
                    row82.createCell(0).setCellValue("产品分组");
                    row82.createCell(1).setCellValue("对标分组");
                    row82.createCell(2).setCellValue("物料名称");
                    row82.createCell(3).setCellValue("业务平台");

                    // Day表头
                    for (int i = 1; i <= maxDays; i++) {
                        row82.createCell((i - 1) + 4).setCellValue("Day" + i);
                    }
                    row82.createCell(maxDays + 4).setCellValue("合计");
                    count8++;
                }

                rowSheet8 = sheet8.createRow(count8);
                rowSheet8.createCell(0).setCellValue(rk1);
                rowSheet8.createCell(1).setCellValue(rk2 == 1 ? "新品" : "对标产品");
                rowSheet8.createCell(2).setCellValue(materialName);
                rowSheet8.createCell(3).setCellValue(businessLine);

                firstDayNumber = 0;
                sum = 0;

                if ("批发".equals(businessLine)) {
                    try {
                        firstDayNumber = fisrtDayNumberMaterialMap.get(materialName);
                    } catch (Exception e) {
                    }
                } else {
                    firstDayNumber = daysMaterial.get(0);
                }
                rowSheet8.createCell(4).setCellValue(firstDayNumber);
                sum += firstDayNumber;
                int dayNumber;
                for (int i = 1; i < maxDays; i++) {
                    dayNumber = daysMaterial.get(i);
                    rowSheet8.createCell((i - 1) + 5).setCellValue(daysMaterial.get(i));
                    sum += dayNumber;
                }
                rowSheet8.createCell(maxDays + 4).setCellValue(sum);
                count8++;
            }
        } catch (Exception e) {
        }

    }
}
