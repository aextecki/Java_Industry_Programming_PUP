public class CompareNumbers {
    public static void main(String[] args) {
        int a = 4;
        int b = 5;
        int result;

        // Check if A is greater than B
        if (a > b) {
            // Subtract if A is greater
            result = a - b;
            System.out.println("A is greater than B. The result of subtraction is: " + result);
        } else {
            // Sum if A is less than or equal to B
            result = a + b;
            System.out.println("A is less than or equal to B. The result of addition is: " + result);
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
    }
}
