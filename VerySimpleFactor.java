public class VerySimpleFactor {
    public static void main(String[] args) {
        int n = 5; // Sample input
        
        if (n < 0) {
            System.out.println("Error: Can't calculate factorial of negative number");
        } else {
            long number = 1;
            for (int i = 2; i <= n; i++) {
                number *= i;
                System.out.println("Multiplying " + number + " by " + i + " to get " + (number * i));   
            }
            System.out.println(n + "! = " + number);
        }
    }
}