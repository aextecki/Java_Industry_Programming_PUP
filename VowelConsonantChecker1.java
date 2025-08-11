import java.util.Scanner;

public class VowelConsonantChecker1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a character: ");
        char inputChar = scanner.next().charAt(0); 
        inputChar = Character.toLowerCase(inputChar); 

        if (Character.isLetter(inputChar)) {
            if ("aeiou".indexOf(inputChar) != -1) {
                System.out.println(inputChar + " is a vowel.");
            } else {
                System.out.println(inputChar + " is a consonant.");
            }
        } else {
            System.out.println("Invalid input! Please enter a letter.");
        }
        scanner.close();
}
}
