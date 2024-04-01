public class Test6 {
    public static void main(String[] args) {
        Object a = "\t hello \t";
        System.out.println(a.toString());
        if (a instanceof String) {
            a = a.toString().replace("\t", "  ");
            System.out.println(a);
        } else {
            System.out.println("X");
        }
    }
}
