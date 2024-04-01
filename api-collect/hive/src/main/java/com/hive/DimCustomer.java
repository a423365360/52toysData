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
public class DimCustomer {
    public static void main(String[] args) throws Exception {
        String inputPath = "d:\\dim\\dim.xlsx";
        String outputPath = "d:\\dim\\c2023-08-16.json";
        FileInputStream fis = new FileInputStream(inputPath);
        FileOutputStream fos = new FileOutputStream(outputPath, false);
        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(fos));
        XSSFWorkbook sheets = new XSSFWorkbook(fis);
        XSSFSheet sheet = sheets.getSheetAt(0);  //TODO sale_time
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            JSONObject json = new JSONObject();
            XSSFRow row = sheet.getRow(i);
            for (int j = 0; j < 5; j++) {
                try {
                    switch (j) {
                        case 0:
                            if ("".equals(row.getCell(j).getStringCellValue().trim())) {
                                break;
                            }
                            json.put("name", row.getCell(j).getStringCellValue());
                            break;
                        case 1:
                            if ("".equals(row.getCell(j).getStringCellValue().trim())) {
                                break;
                            }
                            json.put("line", row.getCell(j).getStringCellValue());
                            break;
                        case 2:
                            json.put("start_time", row.getCell(j).getDateCellValue());
                            break;
                        case 3:
                            json.put("end_time", row.getCell(j).getDateCellValue());
                            break;
                        case 4:
                            json.put("head_time", row.getCell(j).getDateCellValue());
                            break;
                    }
                } catch (Exception e) {
                }
            }
            if (json.get("name") == null || "".equals(json.get("name").toString().trim()) || json.get("line") == null || "".equals(json.get("line").toString().trim())) {
                continue;
            }
            json.put("tail_time", DateTime.now().toTimestamp());
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
