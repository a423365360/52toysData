import com.util.FieldsUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;


// 校验DDL
public class Test4 {
    public static void main(String[] args) throws Exception {

        String fields = FieldsUtil.xls("D:\\SAL_OUTSTOCK.xls");
        String[] fieldList = fields.split(",");

        HashSet<String> all = new HashSet<>();
        HashSet<String> test = new HashSet<>();
        HashSet<String> result = new HashSet<>();

        for (String field :
                fieldList) {
            all.add(field);
        }
        System.out.println("All size is " + all.size());

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\test.txt"), "utf-8"));

        String line;
        while ((line = br.readLine()) != null) {
            test.add(line.split(" ")[0]);
        }
        System.out.println("Test size is " + test.size());


        for (String unit : all) {
            if (!test.contains(unit)) {
                result.add(unit);
            }
        }

        System.out.println("Result size is " + result.size());

        for (String u :
                result) {
            System.out.println(u);
        }

    }
}
