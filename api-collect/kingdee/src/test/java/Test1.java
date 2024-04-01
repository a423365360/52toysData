

public class Test1 {
    public static void main(String[] args) {
        String test = "hello@world";
        String[] split = test.split("@");
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
        }
    }
}
