import java.text.MessageFormat;
import java.util.*;

/**
 * Survive the wrath of Kutulu
 * Coded fearlessly by JohnnyYuge & nmahoude (ok we might have been a bit scared by the old god...but don't say anything)
 **/
class Player {
    public static int LIGHT_RANGE = 5;
    public static int PLAN_RANGE = 5;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        char[][] maze = new char[width][height];
        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
            maze[i] = line.toCharArray();
//            System.err.println(line);
        }
        int sanityLossLonely = in.nextInt(); // how much sanity you lose every turn when alone, always 3 until wood 1
        int sanityLossGroup = in.nextInt(); // how much sanity you lose every turn when near another player, always 1 until wood 1
        int wandererSpawnTime = in.nextInt(); // how many turns the wanderer take to spawn, always 3 until wood 1
        int wandererLifeTime = in.nextInt(); // how many turns the wanderer is on map after spawning, always 40 until wood 1

        // game loop
        Map<Integer, Wanderer> wanderers = new HashMap();
        Map<Integer, Explorer> explorers = new HashMap();
        Explorer myHero = new Explorer();
        while (true) {
            int entityCount = in.nextInt(); // the first given entity corresponds to your explorer
            for (int i = 0; i < entityCount; i++) {
                EntityType entityType = EntityType.valueOf(in.next());
                int id = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int param0 = in.nextInt();
                int param1 = in.nextInt();
                int param2 = in.nextInt();

                //Entity creation
                switch (entityType) {
                    case EXPLORER:
                        Explorer explorer;
                        if (explorers.containsKey(id)) {
                            explorer = explorers.get(id);
                            explorer.update(x, y, param0, param2);
                        } else {
                            explorer = new Explorer(id, x, y, param0, param2);
                            explorers.put(id, explorer);
                        }
                        if (i == 0) {
                            myHero = explorer;
                        }
                        break;
                    case WANDERER:
                        if (wanderers.containsKey(id)) {
                            Wanderer wanderer = wanderers.get(id);
                            if (param1 == 0) {
                                wanderer.setSpawnsIn(param0);
                            } else {
                                wanderer.setDiesIn(param0);
                                wanderer.setSpawnsIn(-1);
                            }
                            wanderer.update(x, y, param2);
                        } else {
                            Wanderer wanderer = new Wanderer(param0);
                            wanderer.setId(id);
                            wanderers.put(id, wanderer);
                        }
                        break;
                }
            }

            //Set Chasing
            myHero.setChasedBy(new ArrayList<>());
            explorers.values().parallelStream()
                .forEach(explorer -> explorer.setChasedBy(new ArrayList<>()));
            wanderers.values().parallelStream()
                .filter(wanderer -> wanderer.getTarget() != -1)
                .forEach(wanderer -> {
//                        System.err.println(MessageFormat.format("wanderer {0} is chasing explorer {1}", wanderer.getId(), wanderer.getTarget()));
                    explorers.get(wanderer.getTarget()).getChasedBy().add(wanderer.getId());
                });

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            String move = myHero.getMove(wanderers, explorers, maze);
            System.out.println(move); // MOVE <x> <y> | WAIT
        }
    }


    public enum Place {
        WALL("#"), PORTAL("w"), EMPTY(".");

        private String value;

        Place(String value) {
            this.value = value;
        }

        public static Place fromName(String value) {
            if (value.equalsIgnoreCase(WALL.getValue())) {
                return WALL;
            } else if (value.equalsIgnoreCase(PORTAL.getValue())) {
                return PORTAL;
            } else if (value.equalsIgnoreCase(EMPTY.getValue())) {
                return EMPTY;
            } else {
                System.err.println("enum constant unkown " + value);
                return null;
            }
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum EntityType {
        EXPLORER, WANDERER, EFFECT_PLAN, EFFECT_LIGHT;
    }

    static class Explorer extends Entity implements Cloneable {

        private int health;
        private List<Integer> chasedBy = new ArrayList<>();
        private int remainingLights;

        public Explorer() {
        }

        public Explorer(int id, int x, int y, int health, int remainingLights) {
            super(id, x, y);
            this.setHealth(health);
            this.remainingLights = remainingLights;
        }

        public Explorer(Explorer explorer) {
            super(explorer.getId(), explorer.getX(), explorer.getY());
            this.health = explorer.getHealth();
            this.chasedBy = explorer.getChasedBy();
            this.remainingLights = explorer.getRemainingLights();
        }

        public void update(int x, int y, int health, int remainingLights) {
            this.setPosition(x, y);
            this.setHealth(health);
            this.setRemainingLights(remainingLights);
        }

        public String getMove(Map<Integer, Wanderer> wanderers,
                              Map<Integer, Explorer> explorers,
                              char[][] maze) {
            Explorer closestExplorer = this.getClosestExplorer(explorers);
            Double closestExplorerDistance = closestExplorer == null ? Double.MAX_VALUE : closestExplorer.getDistanceFrom(this);
            Double closestWandererDistance = this.getClosestChaserDistance(wanderers);
            boolean isChased = (!this.chasedBy.isEmpty() && closestWandererDistance < 5.0) ||
                (closestWandererDistance < 30.0 && closestExplorerDistance >= 2.0) ||
                (closestWandererDistance < 5);

            //If a wandered is chasing the explorer or if one is close
            if (isChased) {
                System.err.println("Escaping");

                List<Explorer> possibleMoves = this.nextPossibleMoves();
                possibleMoves.add(this);
                Explorer bestPossibility = possibleMoves.stream()
                    .filter(explorer -> {//Remove in walls possibilities
                        int x = explorer.getX();
                        int y = explorer.getY();
                        String place = String.valueOf(maze[y][x]);
//                            System.err.println(MessageFormat.format("for x: {0} y: {1} place is: {2}", x, y, place));
                        System.err.println(MessageFormat.format("for x: {0} y: {1} score is: {2}", x, y, explorer.getPositionScore(wanderers)));
                        return !place.equalsIgnoreCase("#");
                    })
                    .max(Comparator.comparing(explorer -> explorer.getPositionScore(wanderers)))
                    .orElse(this);
                double bestScore = bestPossibility.getPositionScore(wanderers);
                if (bestPossibility.isSamePosition(this)) {
                    if (this.getRemainingLights() == 0) {
                        return "WAIT no solution found I don't have lights anymore";
                    }
                    //No escape possible
                    long wanderersInRangeOfLight = wanderers.values().stream()
                        .filter(wanderer -> wanderer.isInLightRange(this))
                        .count();

                    if (wanderersInRangeOfLight > 0) {
                        this.setRemainingLights(this.getRemainingLights() - 1);
                        return "LIGHT";
                    }
                } else {
                    return MessageFormat.format("MOVE {0} {1}", bestPossibility.getX(), bestPossibility.getY());
                }
            } else if (closestExplorerDistance < 15.0) { //looks for buddy to team up if not too far
                System.err.println("looking for a buddy");

                List<Explorer> possibleMoves = this.nextPossibleMoves();
                possibleMoves.add(this);
                Explorer bestPossibility = possibleMoves.stream()
                    .filter(explorer -> {//Remove in walls possibilities
                        int x = explorer.getX();
                        int y = explorer.getY();
                        String place = String.valueOf(maze[y][x]);
//                            System.err.println(MessageFormat.format("for x: {0} y: {1} place is: {2}", x, y, place));
                        System.err.println(MessageFormat.format("for x: {0} y: {1} score is: {2}", x, y, explorer.getPositionScore(wanderers)));
                        return !place.equalsIgnoreCase("#");
                    })
                    .min(Comparator.comparing(explorer -> explorer.getDistanceFrom(closestExplorer)))
                    .orElse(this);

                if (bestPossibility.isSamePosition(this)) {
                    System.err.println("no solution found");
                    return "WAIT";
                } else {
                    return MessageFormat.format("MOVE {0} {1}", bestPossibility.getX(), bestPossibility.getY());
                }
            }

            return "WAIT";
        }


        public Double getPositionScore(Map<Integer, Wanderer> wanderers) {
            double chasersDistance = this.getWeightedDistanceFromChasers(wanderers);
            double otherWanderersDistance = this.getWeightedDistanceFromWanderers(wanderers);

            double closestMalus = this.getClosestChaserDistance(wanderers) < 4 ? 500 : 0;

            double score = chasersDistance + otherWanderersDistance;
            score -= closestMalus;
//            System.err.println("Score is " + score);
            return score;
        }

        public Double getDistanceFromWanderers(Map<Integer, Wanderer> wanderers) {
            Double distance = wanderers.values().stream()
                .filter(wanderer -> wanderer.getTarget() != this.getId())
                .map((wanderer) -> wanderer.getDistanceFrom(this))
                .reduce(Double::sum).orElse(0.0);

//            System.err.println(MessageFormat.format("for x: {0} y: {1} distance from wanderer is: {2}", this.getX(), this.getY(), distance));
            return distance;
        }

        public Double getWeightedDistanceFromWanderers(Map<Integer, Wanderer> wanderers) {
            Double distance = wanderers.values().stream()
                .filter(wanderer -> wanderer.getTarget() != this.getId())
                .map((wanderer) -> wanderer.getDistanceFrom(this))
                .map(aDouble -> {
                    double coeffOtherWanderer = 10;
                    if (aDouble < 5) {
                        coeffOtherWanderer = 0.001;
                    } else if (aDouble < 10) {
                        coeffOtherWanderer = 0.01;
                    } else if (aDouble < 20) {
                        coeffOtherWanderer = 1;
                    }

                    return coeffOtherWanderer * aDouble;
                })
                .reduce(Double::sum).orElse(0.0);

//            System.err.println(MessageFormat.format("for x: {0} y: {1} distance from wanderer is: {2}", this.getX(), this.getY(), distance));
            return distance;
        }

        public Double getDistanceFromChasers(Map<Integer, Wanderer> wanderers) {
            Double distance = this.getChasedBy().stream()
                .map(wanderers::get)
                .map((wanderer) -> wanderer.getDistanceFrom(this))
                .reduce(Double::sum).orElse(0.0);

//            System.err.println(MessageFormat.format("for x: {0} y: {1} distance from chasers is: {2}", this.getX(), this.getY(), distance));
            return distance;
        }

        public Double getWeightedDistanceFromChasers(Map<Integer, Wanderer> wanderers) {
            Double distance = this.getChasedBy().stream()
                .map(wanderers::get)
                .map((wanderer) -> wanderer.getDistanceFrom(this))
                .map(aDouble -> {
                    double coeffChaser = 10;
                    if (aDouble < 5) {
                        coeffChaser = 0.001;
                    } else if (aDouble < 10) {
                        coeffChaser = 0.01;
                    } else if (aDouble < 20) {
                        coeffChaser = 1;
                    }
                    return coeffChaser * aDouble;
                })
                .reduce(Double::sum).orElse(0.0);

//            System.err.println(MessageFormat.format("for x: {0} y: {1} distance from chasers is: {2}", this.getX(), this.getY(), distance));
            return distance;
        }

        public Double getClosestChaserDistance(Map<Integer, Wanderer> wanderers) {
            Double distance = wanderers.values().stream()
                .map((wanderer) -> wanderer.getDistanceFrom(this))
                .min(Double::compareTo)
                .orElse(Double.MAX_VALUE);

            return distance;
        }

        public Explorer getClosestExplorer(Map<Integer, Explorer> explorers) {
            Explorer closest = explorers.values().stream()
                .min(Comparator.comparing((explorer) -> explorer.getDistanceFrom(this)))
                .orElse(null);

            return closest;
        }

        public List<Explorer> nextPossibleMoves() {
            List<Explorer> moves = new ArrayList<>();
            Explorer exp = new Explorer(this);
            exp.setX(exp.getX() - 1);
            moves.add(exp);

            exp = new Explorer(this);
            exp.setY(exp.getY() - 1);
            moves.add(exp);

            exp = new Explorer(this);
            exp.setX(exp.getX() + 1);
            moves.add(exp);

            exp = new Explorer(this);
            exp.setY(exp.getY() + 1);
            moves.add(exp);

            return moves;
        }

        public int getHealth() {
            return health;
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public List<Integer> getChasedBy() {
            return chasedBy;
        }

        public void setChasedBy(List<Integer> chasedBy) {
            this.chasedBy = chasedBy;
        }

        public int getRemainingLights() {
            return remainingLights;
        }

        public void setRemainingLights(int remainingLights) {
            this.remainingLights = remainingLights;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Explorer explorer = (Explorer) o;
            return getHealth() == explorer.getHealth() &&
                getChasedBy().equals(explorer.getChasedBy());
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), getHealth(), getChasedBy());
        }
    }

    static class Wanderer extends Entity {
        private int spawnsIn;
        private int diesIn;
        private int target = -1;

        public Wanderer(int spawnsIn) {
            super();
            this.spawnsIn = spawnsIn;
        }

        public void update(int x, int y, int target) {
            this.setPosition(x, y);
            this.setTarget(target);
        }

        public boolean isInLightRange(Explorer explorer) {
            return this.getDistanceFrom(explorer) < Player.LIGHT_RANGE;
        }

        public int getSpawnsIn() {
            return spawnsIn;
        }

        public void setSpawnsIn(int spawnsIn) {
            this.spawnsIn = spawnsIn;
        }

        public int getTarget() {
            return target;
        }

        public void setTarget(int target) {
            this.target = target;
        }

        public int getDiesIn() {
            return diesIn;
        }

        public void setDiesIn(int diesIn) {
            this.diesIn = diesIn;
        }
    }

    static abstract class Entity {
        private int id;
        private int x;
        private int y;

        public Entity() {
        }

        public Entity(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public double getDistanceFrom(Entity other) {
            return Math.sqrt(Math.pow(other.getX() - this.x, 2) + Math.pow(other.getY() - this.y, 2));
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entity entity = (Entity) o;
            return getId() == entity.getId() &&
                getX() == entity.getX() &&
                getY() == entity.getY();
        }

        public boolean isSamePosition(Entity entity) {
            return getId() == entity.getId() &&
                getX() == entity.getX() &&
                getY() == entity.getY();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), getX(), getY());
        }
    }
}