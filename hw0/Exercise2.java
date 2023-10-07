public class Exercise2 {
    public static void main(String[] args) {
        int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};
        System.out.println(max(numbers));
    }
    public static int max(int[] array) {
        int maxTerm=0;
        for (int j : array) {
            if (j > maxTerm) {
                maxTerm = j;
            }
        }
        return maxTerm;
    }
}
