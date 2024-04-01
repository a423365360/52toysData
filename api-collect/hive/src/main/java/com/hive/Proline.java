package com.hive;

import cn.hutool.core.date.DateTime;
import com.util.ProlineUtil;

public class Proline {
    public static void main(String[] args) throws Exception {
        String formId = args[0];
        String dt = args[1];
        String flag = args[2];
        String directory = args[3];
        String fileName = args[4];

        if ("today".equals(dt)) {
            dt = DateTime.now().toDateStr();
        }

        if ("today".equals(fileName)) {
            fileName = formId + DateTime.now().toDateStr() + "_" + DateTime.now().getTime() + ".log";
        }

        try {
            ProlineUtil.proline(formId, dt, flag, "create", directory, fileName);
            ProcessBuilder bash = new ProcessBuilder("bash", "/home/52toys/bin/proline.sh", formId, dt, directory, fileName);
            Process process = bash.start();
            int exitCode = process.waitFor();
            System.out.println(exitCode);
        } catch (Exception e) {
        }
    }
}
