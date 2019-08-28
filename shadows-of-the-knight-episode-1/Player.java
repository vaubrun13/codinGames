import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int W = in.nextInt(); // width of the building.
        int H = in.nextInt(); // height of the building.
        int N = in.nextInt(); // maximum number of turns before game over.
        int X0 = in.nextInt();
        int Y0 = in.nextInt();

        int batmanX = X0; //Batman's initial position
        int batmanY = Y0;

        int minX = 0;
        int minY = 0;
        int maxX = W;
        int maxY = H;
        // game loop
        while (true) {
            String bombDir = in.next(); // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            int x = batmanX, y = batmanY;
            if (bombDir.contains("R")) {
                x = (batmanX + maxX) / 2;
                minX = batmanX;
            } else if (bombDir.contains("L")) {
                x = (batmanX + minX) / 2;
                maxX = batmanX;
            }

            if (bombDir.contains("D")) {
                y = (batmanY + maxY) / 2;
                minY = batmanY;
            } else if (bombDir.contains("U")) {
                y = (batmanY + minY) / 2;
                maxY = batmanY;
            }


            batmanX = x;
            batmanY = y;
            System.out.println(x + " " + y);
        }
    }
}