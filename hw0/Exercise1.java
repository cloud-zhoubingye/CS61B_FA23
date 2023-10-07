package hw0;

public class Exercise1 {
    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            printStar(i);
        }
    }
    public static void printStar(int n) {
        for (int i = 0; i < n; i++) {
            System.out.print("*");
        }
        System.out.print("\n");
    }
}
