package com.hive;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson2.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

// 上市日期
public class DimSaleTime {
    public static void main(String[] args) throws Exception {
        String inputPath = "d:\\dim\\dim.xlsx";
        String outputPath = "d:\\dim\\t2023-08-16.json";
        FileInputStream fis = new FileInputStream(inputPath);
        FileOutputStream fos = new FileOutputStream(outputPath, false);
        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(fos));
        XSSFWorkbook sheets = new XSSFWorkbook(fis);
        XSSFSheet sheet = sheets.getSheetAt(1);  //TODO sale_time
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            JSONObject json = new JSONObject();
            XSSFRow row = sheet.getRow(i);
            for (int j = 0; j < 12; j++) {
                try {
                    switch (j) {
                        case 0:
                            if ("".equals(row.getCell(j).getStringCellValue().trim())) {
                                break;
                            }
                            json.put("product_series", row.getCell(j).getStringCellValue());
                            break;
                        case 1:
                            if ("".equals(row.getCell(j).getStringCellValue().trim())) {
                                break;
                            }
                            json.put("material_name", row.getCell(j).getStringCellValue());
                            break;
                        case 2:
                            json.put("product_line", row.getCell(j).getStringCellValue());
                            break;
                        case 3:
                            json.put("first_sale_date", row.getCell(j).getDateCellValue());
                            break;
                        case 4:
                            json.put("online_sale_date", row.getCell(j).getDateCellValue());
                            break;
                        case 5:
                            json.put("offline_sale_date", row.getCell(j).getDateCellValue());
                            break;
                        case 6:
                            json.put("note", row.getCell(j).getStringCellValue());
                            break;
                        case 7:
                            json.put("price", row.getCell(j).getNumericCellValue());
                            break;
                        case 8:
                            json.put("model", row.getCell(j).getStringCellValue());
                            break;
                        case 9:
                            json.put("activity", row.getCell(j).getStringCellValue());
                            break;
                        case 10:
                            json.put("ip", row.getCell(j).getStringCellValue());
                            break;
                        case 11:
                            json.put("product_property", row.getCell(j).getStringCellValue());
                            break;
                    }
                } catch (Exception e) {
                }
            }
            if ((json.get("product_series") == null || "".equals(json.get("product_series").toString().trim()))
                    && (json.get("material_name") == null || "".equals(json.get("material_name").toString().trim()))) {
                continue;
            }
            json.put("head_time", DateTime.now().toTimestamp());
            json.put("tail_time", DateTime.of("9999-12-31","yyyy-MM-dd"));
            bf.write(json.toString());
            bf.newLine();
            bf.flush();
        }
        bf.close();
//        // Create a Configuration object.
//        Configuration conf = new Configuration();
//
//        //TODO Get the filesystem object for HDFS.
//        FileSystem fs = FileSystem.get(URI.create("hdfs://8.140.57.104:8020"), conf, "52toys");
//
//        // Create a Path object for the local file.
//        Path localPath = new Path(outputPath);
//
//        // Create a Path object for the HDFS file.
//        Path hdfsPath = new Path("/user/dim/customerDim.json");
//
//        // Copy the local file to HDFS.
//        fs.copyFromLocalFile(localPath, hdfsPath);
    }
}
