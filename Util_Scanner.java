
import java.util.Scanner;

class Util_Scanner{

	public static void main(String[] args){
		
		Scanner xyz = new Scanner(System.in);

        int sub = 0;
        int mul = 0;
        double div = 0;
        int mod = 0;


        int num = 15;

        switch (num) {

        case 5:

            System.out.println("It is 5");

            break;

        case 10:

            System.out.println("It is 10");

            break;

        case 15:

            System.out.println("It is 15");

            break;

        case 20:

            System.out.println("It is 20");

            break;

        default:

            System.out.println("Not present");
        }

        int i = 10;

 

        if (i == 10 || i < 15) {

          
            // First if statement

            if (i < 15)

                System.out.println("i is smaller than 15");

 

            // Nested - if statement

            // Will only be executed if statement above

            // it is true

            if (i < 12)

                System.out.println(

                    "i is smaller than 12 too");

        }

        else {

            System.out.println("i is greater than 15");

        }
        
		System.out.println("Enter an integer: ");
		
		int number1 = xyz.nextInt();
		System.out.println("You Entered " + number1);
		
        System.out.println("Enter Second Number: ");

		double number2 = xyz.nextDouble();
		System.out.println("You Entered " + number2);

        int sum = (int) (number1 + number2);
        sub = (int) (number1 - number2);
        mul = (int) (number1 * number2);
        div = (double) (number1 / number2);
        mod = (int) (number1 % number2);


		System.out.println("The Sum is = " + sum);
        System.out.println("The Difference = " + sub);
        System.out.println("The Product is = " + mul);
        System.out.println ("The Quotient is = " + div);
        System.out.println("The Remainder is = " + mod);
		
		xyz.close();

        

	}

}
