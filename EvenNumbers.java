public class EvenNumbers {
    public static void main(String[] args) {
        System.out.println("Even numbers between 0 and 10:");
        
        for (int i = 0; i <= 10; i++) {
            
            if (i % 2 == 0) {
                System.out.println(i);
            }
        }
    }
}