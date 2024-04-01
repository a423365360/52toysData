import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.util.RedisUtil;
import redis.clients.jedis.Jedis;

import java.io.FileOutputStream;

public class Test2 {
    public static void main(String[] args) throws Exception {
        Jedis jedis = RedisUtil.getJedis("0");
        jedis.set("SAL_OUTSTOCK", "0");
        jedis.set("SAL_RETURNSTOCK", "0");
        jedis.set("PUR_MRB", "0");
        jedis.set("STK_TRANSFEROUT", "0");
        jedis.set("STK_AssembledApp", "0");
        jedis.set("STK_InStock", "0");
        jedis.set("row_SAL_OUTSTOCK", "0");
        jedis.set("row_SAL_RETURNSTOCK", "0");
        jedis.set("row_PUR_MRB", "0");
        jedis.set("row_STK_TRANSFEROUT", "0");
        jedis.set("row_STK_AssembledApp", "0");
        jedis.set("row_STK_InStock", "0");
        jedis.close();
    }
}
