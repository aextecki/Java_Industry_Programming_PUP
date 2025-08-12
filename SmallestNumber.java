import java.util.Scanner;

public class SmallestNumber {
        static int small(int a, int b, int c) {
            if (a < b && a < c) { // check if && or both is true 
                return a;
            } else if (b < a && b < c) { // check if b is less than both a and c
                return b;
            } else {
                return c;
            }
        }

        public static void main(String[] args) {
            int a = 0, b = 0, c = 0; // input numbers
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter three numbers: ");
            a = sc.nextInt();
            b = sc.nextInt();
            c = sc.nextInt();
            sc.close();
            System.out.println("The smallest number is: " + small(a, b, c));
        }   
}
