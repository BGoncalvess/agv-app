public class teste {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello, World!");
        int i = 0;
        while (i <= 100) {
                System.out.println("Printing number:" + i);
                i++;
                Thread.sleep(3000);
        }
    }
}