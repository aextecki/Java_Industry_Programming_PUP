import java.util.Scanner;

public class FutureInvestment {
    public static double futureValue(double money, double rate, int years) {
        // Monthly compounding 
        double monthlyRate = rate / 100 / 12;
        int months = years * 12;
        double result = money;
        for (int i = 0; i < months; i++) {
            result = result * (1 + monthlyRate);
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner mr = new Scanner(System.in);

        System.out.print("Input the investment amount: ");
        double money = mr.nextDouble();

        System.out.print("Input the rate of interest: ");
        double rate = mr.nextDouble();

        System.out.print("Input number of years: ");
        int years = mr.nextInt();

        System.out.println("\nYears    FutureValue");
        for (int i = 1; i <= years; i++) {
            double fv = futureValue(money, rate, i);
            System.out.printf("%-8d %.2f\n", i, fv);
        }

        mr.close();
    }

}

/* The string %-8d %.2f\n in Java is a format string used with methods like System.out.printf() or String.format() for formatted output. It specifies how different data types should be presented in the output.

Here is a breakdown of its components:
  %: This character introduces a format specifier.
-8d: This is a format specifier for an integer.
  -: This flag indicates left-justification within the specified field width.
  8: This is the field width, meaning the integer will occupy at least 8 characters. If the integer is shorter than 8 characters, it will be padded with spaces on the right (due to the - flag).
  d: This is the conversion character for a decimal integer.
 ` `: A literal space character, which will be printed as is.
%.2f: This is a format specifier for a floating-point number (like float or double).
 .2: This is the precision specifier, indicating that the floating-point number should be displayed with exactly two digits after the decimal point.
  f: This is the conversion character for a floating-point number in decimal format.
\n: This is an escape sequence representing a newline character, which moves the cursor to the beginning of the next line.

In summary, this format string instructs Java to: 
Print an integer, left-justified in a field of 8 characters.
Print a literal space.
Print a floating-point number with two decimal places.
Then, move to the next line. */