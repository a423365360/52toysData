import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadMehtod {
    public static String line;

    public static void main(String[] args) throws Exception {

        FileInputStream fis = new FileInputStream("d:\\gy.txt");

        InputStreamReader isr = new InputStreamReader(fis, "utf-8");

        BufferedReader br = new BufferedReader(isr);

        FileOutputStream fos = new FileOutputStream("D:\\result.txt");


        while ((line = br.readLine()) != null) {
            if (line.contains("（gy")) {
//                String split = line.split("（")[1].split("）")[0];
                line = line.replace("  "," ");
                String split = line.split(" ")[1].split("（")[0];
                System.out.println(split);

//                fos.write(line.getBytes());
//                fos.write("\n".getBytes());
            }
        }
    }
}
