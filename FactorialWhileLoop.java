public class FactorialWhileLoop {
    public static void main(String[] args) {
        int n = 5;
        int product = 1;
        int i = n; 

        System.out.println("Calculating the factorial of " + n + " using a while loop...");
        if (n < 0) {
            System.out.println("Factorial is not defined for negative numbers.");
            return;
        }

        while (i > 0) {
            product *= i;
            i--;
        }

        System.out.println("The product of all positive integers ≤ " + n + " is: " + product);
    }
}