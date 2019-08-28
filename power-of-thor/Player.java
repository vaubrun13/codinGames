import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 * ---
 * Hint: You can use the debug stream to print initialTX and initialTY, if Thor seems not follow your orders.
 **/
class Player {
    static int xPosition;
    static int yPosition;
    static int maxX = 39;
    static int maxY = 17;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int lightX = in.nextInt(); // the X position of the light of power
        int lightY = in.nextInt(); // the Y position of the light of power
        int initialTx = in.nextInt(); // Thor's starting X position
        int initialTy = in.nextInt(); // Thor's starting Y position

        xPosition = initialTx;
        yPosition = initialTy;
        // game loop
        while (true) {
            int remainingTurns = in.nextInt(); // The remaining amount of turns Thor can move. Do not remove this line.

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            int xDirection = Player.getVectorDirection(initialTx, lightX);
            int yDirection = Player.getVectorDirection(initialTy, lightY);
            String direction = Player.mapVectorToCadinal(xDirection, yDirection);
            // A single line providing the move to be made: N NE E SE S SW W or NW
            System.out.println(direction);
        }
    }

    public static int getVectorDirection(int start, int end) {
        return end - start;
    }

    public static String mapVectorToCadinal(int x, int y) {
        String result = "";
        if (y > 0 && Player.yPosition < Player.maxY) {
            result += "S";
            Player.yPosition++;
        } else if (y < 0) {
            result += "N";
            Player.yPosition--;
        }

        if (x > 0 && Player.yPosition < Player.maxX) {
            result += "E";
            Player.xPosition++;
        } else if (x < 0) {
            result += "W";
            Player.xPosition--;
        }

        return result;

    }
}  