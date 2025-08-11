import java.util.Scanner;

public interface SumOfIntegers {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of integers you want to sum: ");
        int n = scanner.nextInt();

        int sum = 0;
        for (int i = 0; i < n; i++) {
            System.out.print("Enter integer " + (i + 1) + ": ");
            int number = scanner.nextInt();
            sum += number;
        }

        System.out.println("The sum of the entered integers is: " + sum);
        scanner.close();
    }

}
