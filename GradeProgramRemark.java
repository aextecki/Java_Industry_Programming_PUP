import java.util.Scanner;

public class GradeProgramRemark {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter your grade (A, B, C, D, or F): ");
        char grade = scanner.next().charAt(0);
        
        grade = Character.toUpperCase(grade);
        
        switch(grade) {
            case 'A':
                System.out.println("Excellent! Keep up the goal.");
                break;
            case 'B':
                System.out.println("Very good! You're can reach the stars.");
                break;
            case 'C':
                System.out.println("Good, but more work and you will be great soon.");
                break;
            case 'D':
                System.out.println("You passed, but need to grind harder.");
                break;
            case 'F':
                System.out.println("Sorry, you failed. Please try again.");
                break;
            default:
                System.out.println("Invalid grade entered. Please enter A, B, C, D, or F.");
        }
        
        scanner.close();
    }
} 
    

