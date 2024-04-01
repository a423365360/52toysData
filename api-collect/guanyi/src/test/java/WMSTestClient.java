
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class WMSTestClient {
    public static Logger logger = Logger.getLogger(Object.class);
    public static Gson gson = new Gson();

    public WMSTestClient() {
    }

    public static void main(String[] args) throws Exception {
        searchInventory();
    }

    public static void searchInventory() {
        String url = "http://116.205.3.183:3333/WebApi/Api/Inventory/SearchInventory";
        StringBuilder requestContent = new StringBuilder();
        requestContent.append("appkey=");
        requestContent.append("&appsecret=");
        requestContent.append("&clientno=");
        requestContent.append("&data=");
        Map<String, String> data = new LinkedHashMap();
        data.put("whcode", "CK_000");
        data.put("skucode", "232545,12344");
        requestContent.append(gson.toJson(data));
        sendPost(url, requestContent.toString());
    }

    public static void createPurchaseAsn() {
        String url = "http://116.205.3.183:3333/WebApi/Api/ASN/CreatePurchaseAsn";
        StringBuilder requestContent = new StringBuilder();
        requestContent.append("appkey=");
        requestContent.append("&appsecret=");
        requestContent.append("&clientno=");
        requestContent.append("&data=");
        Map<String, Object> data = new LinkedHashMap();
        data.put("orderNo", "ABC000601");
        data.put("warehouse", "CK_001");
        data.put("supplierId", "001");
        data.put("apuser", "");
        data.put("aptime", "");
        data.put("formNo", "");
        data.put("createUserName", "");
        data.put("createDate", "");
        data.put("auditUserName", "");
        data.put("auditDate", "");
        data.put("reason", "");
        List<Map<String, String>> detail = new ArrayList();
        Map<String, String> item = new LinkedHashMap();
        item.put("sku", "3111111");
        item.put("qty", "2");
        item.put("storage", "");
        item.put("price", "100");
        detail.add(item);
        data.put("detail", detail);
        requestContent.append(gson.toJson(data));
        sendPost(url, requestContent.toString());
    }

    public static void sendPost(String url, String requestConent) {
        logger.info("url: " + url);
        logger.info("request: " + requestConent);

        try {
            CloseableHttpClient httpclient = null;
            CloseableHttpResponse httpresponse = null;

            try {
                httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(url);
                StringEntity stringentity = new StringEntity(requestConent, ContentType.create("text/plain", "UTF-8"));
                httppost.setEntity(stringentity);
                httpresponse = httpclient.execute(httppost);
                String response = EntityUtils.toString(httpresponse.getEntity());
                logger.info("response: " + response);
            } finally {
                if (httpclient != null) {
                    httpclient.close();
                }

                if (httpresponse != null) {
                    httpresponse.close();
                }

            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }
}
