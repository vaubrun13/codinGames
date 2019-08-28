import java.util.Scanner;

/**
 * This code automatically collects game data in an infinite loop. It uses the
 * standard input to place data into the game variables such as x and y. YOU DO
 * NOT NEED TO MODIFY THE INITIALIZATION OF THE GAME VARIABLES.
 **/
class Player {
    static boolean boostUsed = false;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int x = in.nextInt();
            int y = in.nextInt();
            int nextCheckpointX = in.nextInt(); // x position of the next check point
            int nextCheckpointY = in.nextInt(); // y position of the next check point
            int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            int nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next
            // checkpoint
            int opponentX = in.nextInt();
            int opponentY = in.nextInt();
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messagesde...");

            // Edit this line to output the target position
            // and thrust (0 <= thrust <= 100)
            // i.e.: "x y thrust"

            String thrust = getThrust(nextCheckpointDist, nextCheckpointAngle);
            System.out.println(nextCheckpointX + " " + nextCheckpointY + " " + thrust);
        }
    }

    private static String getThrust(int nextCheckpointDist, int nextCheckpointAngle) {
        int absAngle = Math.abs(nextCheckpointAngle);
        // According to distance
        if (nextCheckpointDist < 900) {
            return "0";
        } else if (nextCheckpointDist < 1500) {
            return "50";
        } else if (nextCheckpointDist < 2500) {
            return "70";
        }
        // According to angle
        if (absAngle > 90) {
            return "0";
        } else if (absAngle > 45) {
            return "50";
        } else if (absAngle > 20) {
            return "85";
        } else if (Player.boostUsed) {
            return "100";
        } else {
            Player.boostUsed = true;
            return "BOOST";
        }

    }

    private static int getVectorDirection(int start, int end) {
        return end - start;
    }

    private static double CalculateDistance(int x, int y) {
        return Math.sqrt(Math.pow(y, 2) - Math.pow(x, 2));
    }
}