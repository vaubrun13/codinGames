import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {
    static int maxTemp = 5526;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // the number of temperatures to analyse
        int min = maxTemp;
        for (int i = 0; i < n; i++) {
            int t = in.nextInt(); // a temperature expressed as an integer ranging from -273 to 5526
            if (Math.abs(t) == Math.abs(min) && t > min) {
                min = t;
            } else if (Math.abs(t) < Math.abs(min)) {
                min = t;
            }
        }
        if (min == Solution.maxTemp && n == 0) {
            min = 0;
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(min);
    }
}