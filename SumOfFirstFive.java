public class SumOfFirstFive {
    public static void main(String[] args) {
        int count = 5;  // Start from 5
        int sum = 0;    // Initialize sum
        
        while (count > 0) {
            sum += count;
            count--;
            System.out.println("Current count: " + count + ", Current sum: " + sum);
        }
        
        System.out.println("The sum of the first 5 positive integers is: " + sum);
    }
}