public class Exercise3 {
    public static void windowPosSum(int[] a, int n) {
        /** your code here */
        for (int i = 0; i < a.length-1; i++) {
            if (a[i]<0) continue;
            int temp=0;
            for (int j = 0; j <= n; j++) {
                if ((i+j)<a.length) {
                    temp+=a[i+j];
                } else break;
            }
            a[i]=temp;
        }
    }

    public static void main(String[] args) {
        int[] a = {1, 2, -3, 4, 5, 4};
        int n = 3;
        windowPosSum(a, n);

        // Should print 4, 8, -3, 13, 9, 4
        System.out.println(java.util.Arrays.toString(a));
    }
}