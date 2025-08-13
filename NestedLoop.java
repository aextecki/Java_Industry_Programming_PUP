public class NestedLoop { 
        public static void printer() { //declaring static Non Static method
            for (int x = 1; x <=5; x++){ // outer loop for x 
                for (int y = 1; y <= x; y++){ // nested loop for y
                    System.out.print(x); 
                }
                    System.out.println();
            }
        }
        public static void main(String[] args){
            printer(); // call the Non Static method 
        }
}