package com.util;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.apache.log4j.Logger;

public class SignUtil {
    private static Logger logger = Logger.getLogger(Object.class);

    public SignUtil() {
    }

    public static String sign(String str, String secret) {
        StringBuilder enValue = new StringBuilder();
        enValue.append(secret);
        enValue.append(str);
        enValue.append(secret);
        return encryptByMD5(enValue.toString());
    }

    private static String encryptByMD5(String data) {
        String re_md5 = new String();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] b = md.digest();
            StringBuffer buf = new StringBuffer();

            for (int offset = 0; offset < b.length; ++offset) {
                int i = b[offset];
                if (i < 0) {
                    i += 256;
                }

                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        }

        return re_md5.toUpperCase();
    }

    public static String compare(String jsonStr, String secret) {
        logger.info(jsonStr);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonStr).getAsJsonObject();
        logger.info(jsonObject.toString());
        logger.info("source compare: " + Objects.equals(jsonStr, jsonObject.toString()));
        String sign1 = "null";
        JsonElement signElement = jsonObject.remove("sign");
        if (signElement != null) {
            sign1 = signElement.getAsString();
        }

        logger.info("sign1: " + sign1);
        StringBuilder enValue = new StringBuilder();
        enValue.append(secret);
        enValue.append(jsonObject.toString());
        enValue.append(secret);
        String sign2 = encryptByMD5(enValue.toString());
        logger.info("sign2: " + sign2);
        logger.info("sign compare: " + Objects.equals(sign1, sign2));
        jsonObject.addProperty("sign", sign2);
        logger.info(jsonObject.toString());
        return jsonObject.toString();
    }


}
