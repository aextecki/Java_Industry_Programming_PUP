import java.util.Scanner;

public class Average {
  public static int average(int x, int y, int z) {
    return (x + y + z) / 3;
}

    public static void main(String[] args) {
        int x = 0, y = 0, z = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter first number: ");
        x = scanner.nextInt();
        System.out.print("Enter second number: ");
        y = scanner.nextInt();
        System.out.print("Enter third number: ");
        z = scanner.nextInt();
        scanner.close();    
        System.out.println("Average: " + average(x, y, z));
    }


}