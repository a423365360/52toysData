import com.util.RedisUtil;
import redis.clients.jedis.Jedis;

public class RedisClean {
    public static void main(String[] args) throws Exception {
        Jedis jedis = RedisUtil.getJedis("0");

//        jedis.set("sal_outstock", "0");
//        jedis.set("pur_mrb", "0");
//        jedis.set("stk_transferout", "0");
//        jedis.set("sal_returnstock", "0");
//        jedis.set("stk_assembledapp", "0");
//        jedis.set("stk_instock", "0");
//
//        jedis.set("row_pur_mrb", "0");
//        jedis.set("row_sal_returnstock", "0");
//        jedis.set("row_stk_transferout", "0");
//        jedis.set("row_stk_assembledapp", "0");
//        jedis.set("row_sal_outstock", "0");
//        jedis.set("row_stk_instock", "0");

//        jedis.set("row_history_pur_mrb", "0");
//        jedis.set("row_history_sal_returnstock", "0");
//        jedis.set("row_history_stk_transferout", "0");
//        jedis.set("row_history_stk_assembledapp", "0");
//        jedis.set("row_history_sal_outstock", "0");
//        jedis.set("row_history_stk_instock", "0");

//        jedis.set("history_sal_outstock", "2019-01-05");
//        jedis.set("history_sal_returnstock", "2019-01-05");
//        jedis.set("test_history_sal_outstock", "2019-01-05");
//        jedis.set("test_history_sal_returnstock", "2019-01-05");
//        jedis.set("test_row_history_sal_outstock", "0");
//        jedis.set("test_row_history_sal_returnstock", "0");

//        jedis.set("test_history_bd_material", "2019-12-01");
//        jedis.set("test_row_history_bd_material", "0");
        jedis.set("row_history_bd_material", "0");

        jedis.close();
    }
}
