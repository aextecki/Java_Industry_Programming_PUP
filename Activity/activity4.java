package Activity;
import java.util.Scanner;

public class activity4 {

    public static void main(String[] args){

        double witholdingTaxRate = 0.15; // 15% of witholding tax rate 
        Scanner scanme = new Scanner(System.in);
        System.out.print("Know your Worth!");
        
        System.out.print("\nHourly Rate:");
        double hourlyRate = scanme.nextDouble();

        System.out.print("Hours Work:");
        int hoursWorked = scanme.nextInt();

        double grossPay =  hourlyRate * hoursWorked;
        double witholdingTax = grossPay * witholdingTaxRate;
        double netPay = grossPay - witholdingTax;
        

        System.out.print("Grosspay:" + grossPay);
        System.out.print("\nwitholdingtax:" + witholdingTax);
        System.out.print("\nNetpay:"+ netPay);

        scanme.close(); 

    }
    
}
