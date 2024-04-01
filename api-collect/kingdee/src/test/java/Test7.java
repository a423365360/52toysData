import com.alibaba.fastjson2.JSONArray;
import com.util.CollectUtil;

public class Test7 {
    public static void main(String[] args) throws Exception {
        int count = 0;
        do {
            try {
                int c = 0 / 0;
            } catch (Exception e) {
                count++;
                System.out.println(count);
            }
        } while (count <= 5);
    }
}