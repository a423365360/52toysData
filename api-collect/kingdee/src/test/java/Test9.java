import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.util.List;
import java.util.Properties;


public class Test9 {
    private static String url = "https://52toys.ik3cloud.com/k3cloud/Kingdee.K3.SCM.WebApi.ServicesStub.InventoryQueryService.GetInventoryData.common.kdsvc";
    private static String auh = "https://52toys.ik3cloud.com/k3cloud/Kingdee.BOS.WebApi.ServicesStub.AuthService.LoginByAppSecret.common.kdsvc";

    public static void main(String[] args) throws Exception {
        Properties property = new Properties();
        InputStream resourceAsStream = Test9.class.getClassLoader().getResourceAsStream("kdwebapi_pro.properties");
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "utf-8");
        property.load(inputStreamReader);

        JSONObject auhJson = new JSONObject();
        auhJson.put("acctID", property.getProperty("X-KDApi-AcctID"));
        auhJson.put("username", property.getProperty("X-KDApi-UserName"));
        auhJson.put("appid", property.getProperty("X-KDApi-AppID"));
        auhJson.put("appsecret", property.getProperty("X-KDApi-AppSec"));
        auhJson.put("lcid", property.getProperty("X-KDApi-LCID"));

        HttpRequest postAuh = HttpUtil.createPost(auh);
        HttpRequest body = postAuh.body(auhJson.toString());
        HttpResponse response = body.execute();
        List<HttpCookie> cookies = response.getCookies();
        int flag = JSONObject.parse(response.body()).getIntValue("LoginResultType");

        if (flag == 1) {
            HttpRequest post = HttpUtil.createPost(url);
            String json = "{\"parameters\":\"[{\\\"fstockorgnumbers\\\":\\\"\\\",\\\"fmaterialnumbers\\\":\\\"\\\"," +
                    "\\\"fstocknumbers\\\":\\\"CK070\\\",\\\"flotnumbers\\\":null,\\\"isshowstockloc\\\":true,\\\"isshowauxprop\\\":true," +
                    "\\\"pageindex\\\":1,\\\"pagerows\\\":100}]\"}";
            System.out.println(json);
            post.body(json);
            post.cookie(cookies);

            HttpResponse stockReponse = post.execute();
            System.out.println(stockReponse.body());
        }
    }
}
