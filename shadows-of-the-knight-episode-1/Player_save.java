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

        int x = X0;
        int y = Y0;

        // game loop
        while (true) {
            String bombDir = in.next(); // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            x += Player.getJumpX(bombDir);
            y += Player.getJumpY(bombDir);

            //Some kind of "boost"
            if (y == 0 && N > 3) {
                x *= 4;
            } else if (y == 0 && N <= 3) {
                x *= 2;
            }

            if (x == 0 && N > 3) {
                y *= 4;
            } else if (x == 0 && N <= 3) {
                y *= 2;
            }

            //Boundaries 
            System.err.println("N: " + N);
            x = Math.max(x, 0);
            x = Math.min(x, W - 1);

            y = Math.max(y, 0);
            y = Math.min(y, H - 1);

            // the location of the next window Batman should jump to.
            N--;
            System.out.println(x + " " + y);
        }
    }

    public static int getJumpY(String bombDirection) {
        int y = 0;
        if (bombDirection.contains("D")) {
            y = 1;
        } else if (bombDirection.contains("U")) {
            y = -1;
        } else {
            y = 0;
        }

        return y;
    }

    public static int getJumpX(String bombDirection) {
        int x = 0;
        if (bombDirection.contains("R")) {
            x = 1;
        } else if (bombDirection.contains("L")) {
            x = -1;
        } else {
            x = 0;
        }

        return x;
    }
}