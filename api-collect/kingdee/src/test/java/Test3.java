import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class Test3 {
    public static void main(String[] args) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("o", "1\r\n2");
        System.out.println(jsonObject);;
        System.out.println(jsonObject.get("o"));
    }
}
