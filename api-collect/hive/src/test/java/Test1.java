import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.sql.Date;

public class Test1 {
    public static void main(String[] args) {
        Date date = DateTime.now().toSqlDate();
        Date date1 = DateTime.now().offset(DateField.HOUR, -24 * 5).toSqlDate();

        long between = DateUtil.between(date, date1, DateUnit.DAY, false);
        System.out.println(between);
    }
}
