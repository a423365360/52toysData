
import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.math.BigDecimal;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;


//  TODO 人民币汇率
public class RMB {
    public static void main(String[] args) throws Exception {
        String today = DateTime.now().toDateStr();
        HttpRequest get = HttpUtil.createGet("https://www.chinamoney.com.cn/chinese/bkccpr/");
        HttpResponse response = get.execute();
        List<HttpCookie> cookies = response.getCookies();
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("startDate", today);
        payload.put("endDate", today);
        payload.put("currency", "");   //TODO 币种
        HttpRequest post = HttpUtil.createPost("https://www.chinamoney.com.cn/ags/ms/cm-u-bk-ccpr/CcprHisNew");
        post.cookie(cookies);
        post.form(payload);
        HttpResponse result = post.execute();
        JSONObject jsonObject = JSONObject.parse(result.body());
        JSONArray heads = jsonObject.getJSONObject("data").getJSONArray("head");
        JSONArray rates = jsonObject.getJSONArray("records").getJSONObject(0).getJSONArray("values");
        HashMap<String, BigDecimal> exchangeMap = new HashMap<>();
        for (int i = 0; i < heads.size(); i++) {
            exchangeMap.put(heads.getString(i), BigDecimal.valueOf(rates.getDoubleValue(i)));
        }
        for (String key : exchangeMap.keySet()) {
            System.out.println(key + ": " + exchangeMap.get(key));
        }
    }
}
