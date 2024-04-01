public class Test8 {
    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("bash", "/home/52toys/bin/change.sh");
        Process process = pb.start();
    }
}
