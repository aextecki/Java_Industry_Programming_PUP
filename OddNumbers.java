public class OddNumbers {
    public static void main(String[] args) {
        System.out.println("Odd numbers between 0 and 50:");
        
        for (int i = 0; i <= 50; i++) {
            
            if (i % 2 != 0) {
                System.out.println(i);
            }
        }
    }

}
