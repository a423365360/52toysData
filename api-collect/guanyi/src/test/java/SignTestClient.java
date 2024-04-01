import com.util.HttpUtil;

import java.util.LinkedHashMap;
import java.util.Map;


public class SignTestClient {
    public SignTestClient() {
    }

    public static void main(String[] args) {
        Map<String, Object> data = new LinkedHashMap();
        data.put("start_date", "2015-04-10 17:22:11");
        data.put("end_date", "2015-09-10 17:23:10");
        HttpUtil.sendPost("http://api.guanyierp.com/rest/erp_open", "140233", "562d0c63d8e244928a41990507a96b0d", "d569dd0a1b1c404ca1d188277d209dfe", "2.0", "gy.erp.new.stock.get", "{\"start_date\" : \"2015-04-10 17:22:11\",\"end_date\" : \"2015-09-10 17:23:10\"}");
    }
}

