
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SGTest {
    public SGTest() {
    }

    public static void main(String[] args) throws Exception {
        String[] data = new String[]{"昭文将军", "虎威将军", "辅国将军", "扬武将军", "伏波将军", "镇军将军", "讨逆将军", "鹰扬将军", "折冲将军", "荡寇将军", "御史中丞", "尚书令", "中常侍", "长史", "侍中", "太史大夫", "骠骑将军", "安南将军", "卫将军", "龙骧将军", "征虏将军", "前将军", "平北将军", "车骑将军", "镇西将军", "征东将军", "司徒", "太尉", "大司马", "大司农", "太傅", "司空", "大将军", "丞相"};
        Map<String, Integer> map1 = new HashMap();
        Map<Integer, String> map2 = new HashMap();

        for (int i = 0; i < data.length; ++i) {
            map1.put(data[i], i);
            map2.put(i, data[i]);
        }

        BufferedReader br = new BufferedReader(new FileReader("D:/info.txt"));

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            String name = line.split(":")[0];
            String[] des = line.split(":")[1].split("/");
            int[] temp = new int[des.length];

            for (int i = 0; i < des.length; ++i) {
                temp[i] = (Integer) map1.get(des[i]);
            }

            Arrays.sort(temp);
            StringBuffer sb = new StringBuffer();
            sb.append(name + ":");

            for (int i = 0; i < temp.length; ++i) {
                sb.append((String) map2.get(temp[i]) + "/");
            }

            System.out.println(sb.toString());
        }

        br.close();
    }
}
