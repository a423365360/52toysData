import com.util.HttpUtil;

public class PerformanceTestClient {
    public PerformanceTestClient() {
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.println("begin：" + startTime);

        for (int i = 1; i <= 20; ++i) {
            if (i < 10) {
                System.out.print("0" + i + " ---> ");
            } else {
                System.out.print(i + " ---> ");
            }

            String requestContent = "{\"operator\":\"Aaron\"," +
                    "\"wave_code\":\"BC71174662540\"," +
                    "\"status\":1," +
                    "\"appkey\":\"136012\"," +
                    "\"method\":\"gy.erp.wave.update\"," +
                    "\"sessionkey\":\"c3598d1bfcc949f39aed176671764923\"," +
                    "\"sign\":\"09B9468297CD0F99BF56802844F270E2\"}";
            HttpUtil.sendPost("http://api.guanyierp.com/rest/erp_open", requestContent);
            System.out.println("ok...");
        }

        long endTime = System.currentTimeMillis();
        System.out.println("begin：" + endTime);
        System.out.println("totleTime：" + (endTime - startTime));
    }
}
