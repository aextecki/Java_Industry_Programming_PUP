package Activity;

public class activity3 {
    public static void main(String[] args) {
        int a = 2, b = 4;
        int product = a * b;
        char c = 'c';
        boolean Sagot = true;
        double Pi = 3.14;

        System.out.printf("The value of A is %d while B is %d\n", a, b);
        System.out.println("Letter " + c);
        System.out.println("Initial value of Sagot is " + Sagot);
        System.out.println("Pi contains the value " + Pi);
        
        Sagot = false;
        System.out.println("Sagot is now " + Sagot);
        System.out.println("Is A greater than B? " + (a > b));
        System.out.println(a + " * " + b + " = " + product);
    }
}