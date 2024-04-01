import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

public class Test1 {
    public static void main(String[] args) {
        long startTime = 1700554764600l;

        String s = DateTime.of(startTime).toTimestamp().toString();
        System.out.println(s);
    }
}
