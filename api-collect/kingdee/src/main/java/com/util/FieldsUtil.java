package com.util;


import jxl.Sheet;
import jxl.Workbook;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

// TODO 提取字段
public class FieldsUtil {

    //TODO HTML
    public static String xls(String pathName) throws Exception {
        Workbook workbook = Workbook.getWorkbook(new File(pathName));

        HashSet<String> set = new HashSet<>();
        StringBuilder string = new StringBuilder();

        for (int page = 0; page < workbook.getNumberOfSheets(); page++) {
            Sheet sheet = workbook.getSheet(page);
            //TODO i=0表头
            for (int i = 1; i < sheet.getRows(); i++) {
                /*
                0: name            字段名称
                1: sign            标识
                2: code            物理字段
                3: type            元素类型
                4: must            比录
                5: length          长度
                6: defaultValue    缺省值
                 */
                String field = sheet.getCell(1, i).getContents();
                if ("".equals(field)) {
                    continue;
                }
                set.add(field);
            }
        }

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            if (string.length() > 0) {
                string.append(",");
            }
            string.append(it.next());
        }

        workbook.close();

        return string.toString();
    }

    //TODO HTML中文
    public static HashMap<String, String> xlsMap(String pathName) throws Exception {
        Workbook workbook = Workbook.getWorkbook(new File(pathName));

        HashMap<String, String> map = new HashMap<>();

        for (int page = 0; page < workbook.getNumberOfSheets(); page++) {
            Sheet sheet = workbook.getSheet(page);

            //TODO i=0表头
            for (int i = 1; i < sheet.getRows(); i++) {
                 /*
                0: name            字段名称
                1: sign            标识
                2: code            物理字段
                3: type            元素类型
                4: must            比录
                5: length          长度
                6: defaultValue    缺省值
                 */
                String key = sheet.getCell(1, i).getContents();
                if ("".equals(key)) {
                    continue;
                }
                String value = sheet.getCell(0, i).getContents();

                map.put(key,
                        value);
            }
        }

        workbook.close();
        return map;
    }
}