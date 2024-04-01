import com.alibaba.fastjson2.JSONObject;
import com.config.ConfigInfo;
import com.util.HttpUtil;
import com.util.JsonUtil;
import com.util.SignUtil;

public class Test1 {
    public static void main(String[] args) throws Exception {
        // SDO673766261852 SDO673582611186
        JSONObject jsonData3 = JsonUtil.baseJson();
//        jsonData3.put("method", "gy.erp.trade.history.detail.get");
//        jsonData3.put("code", "SDO673582611186");
//        String sign3 = SignUtil.sign(jsonData3.toString(), ConfigInfo.SECRET);
//        jsonData3.put("sign", sign3);
//        HttpUtil.getGuanyiData(ConfigInfo.URL, jsonData3.toString(), "D:\\test\\test1000.json");

// gy.erp.trade.detail.get
        jsonData3 = JsonUtil.baseJson();
        jsonData3.put("method", "gy.erp.trade.detail.get");
        jsonData3.put("code", "SDO673647891341");
        String sign3 = SignUtil.sign(jsonData3.toString(), ConfigInfo.SECRET);
        jsonData3.put("sign", sign3);
        HttpUtil.getGuanyiData(ConfigInfo.URL, jsonData3.toString(), "D:\\test\\test1111.json");
    }
}
