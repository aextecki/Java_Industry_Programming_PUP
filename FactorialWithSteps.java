public class FactorialWithSteps {
    public static void main(String[] args) {
        int n = -5; // Example negative input
        int i = 1;
        long product = 1;
        
        if (n < 0) {
            System.out.println("Cannot calculate factorial of negative number (" + n + ")");
            System.out.println("Factorial is only defined for non-negative integers.");
            return; // Exit the program
        }
        
        System.out.println("Calculating " + n + "! step by step:");
        System.out.println("Start: product = 1");
        
        do {
            System.out.print("Step " + i + ": Multiply " + product + " × " + i);
            product *= i;
            System.out.println(" → product = " + product);
            i++;
        } while (i <= n);
        
        System.out.println("\nFinal product: " + product);
    }
}