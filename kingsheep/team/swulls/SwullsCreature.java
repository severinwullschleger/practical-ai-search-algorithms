package kingsheep.team.swulls;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

import java.util.*;

/**
 * Created by kama on 01.03.16.
 */
public abstract class SwullsCreature extends Creature {

    private HashMap<String, Square> visitedSquares;
    private ArrayList<Square> squareQueue;
    private ArrayList<Square> squareQueueToAdd;
    private Type map[][];
    protected Type objective[];


    public SwullsCreature(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    public String getNickname() {
        return "Walti Ruedisueli";
    }

    protected Move getMove(Type map[][], char[] objective) {
        visitedSquares = new HashMap<String, Square>();
        squareQueue = new ArrayList<Square>();

        this.map = map;
        this.objective = new Type[objective.length];
        for (int i = 0; i < objective.length; ++i) {
            this.objective[i] = Type.getType(objective[i]);
        }

        Square root = new Square(map[y][x], x, y, this.objective, null, null);
        squareQueueToAdd = new ArrayList<Square>();
        return root.aStarThroughEnvironmentUntilObjectiveIsReached();
    }

    private void printMap(Type map[][]) {
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[0].length; ++j) {
                System.out.print(map[i][j].ordinal());
            }
            System.out.println("");
        }
        System.out.println("-------------------");
    }

    abstract protected char[] getObjectives();

    protected List<String> findObjectives(Type[] objectives) {
        List<Type> objectiveList = Arrays.asList(objectives);
        List<String> objectivePositions = new ArrayList<String>();
        for (int iy = 0; iy < map.length; iy++) {
            for (int ix = 0; ix < map.length; ix++) {
                if (objectiveList.contains(map[iy][ix])) {
                    objectivePositions.add(iy + "_" + ix);
                }
            }
        }
        return objectivePositions;
    }

    class Square {
        Type type;
        private int x, y;
        private Type objective[];
        private Move howToGetHere;
        private Square gotHereFrom;

        protected Square(Type type, int x, int y, Type objective[], Move howToGetHere, Square gotHereFrom) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.objective = objective;
            this.howToGetHere = howToGetHere;
            this.gotHereFrom = gotHereFrom;
        }

        protected Move breadthFirstThroughEnvironmentUntilObjectiveIsReached() {
            squareQueue.add(this);
            visitedSquares.put(this.getStringCoordinate(), this);

            while (squareQueue.size() > 0) {
                Iterator<Square> iter = squareQueue.iterator();
                while (iter.hasNext()) {
                    Square square = iter.next();

                    Move successMove = square.processSquareInQueue();
                    if (successMove != null) {
                        return successMove;
                    }
                }

                squareQueue = squareQueueToAdd;
                squareQueueToAdd = new ArrayList<Square>();
            }


            return Move.WAIT;
        }

        protected Move aStarThroughEnvironmentUntilObjectiveIsReached() {
            squareQueue.add(this);
            visitedSquares.put(this.getStringCoordinate(), this);

            Move successMove = null;
            boolean isFirstCalculation = true;
            int lowestCostsOverall = 0;

            // always calculate till fringe is empty
            while (squareQueue.size() > 0) {

                int lowestCosts = squareQueue.get(0).getTotalEstimatedCosts();
                Square lowestCostSquare = squareQueue.get(0);

                for (int i = 1; i < squareQueue.size(); i++) {
                    Square square = squareQueue.get(i);
                    int costs = squareQueue.get(i).getTotalEstimatedCosts();
                    if (costs < lowestCosts) {
                        lowestCostSquare = square;
                        lowestCosts = costs;
                    }
                }
                squareQueue.remove(lowestCostSquare);
                Move move = lowestCostSquare.processSquareInQueue();

                if (successMove == null || isFirstCalculation || lowestCosts < lowestCostsOverall) {
                    successMove = move;
                    lowestCostsOverall = lowestCosts;
                    isFirstCalculation = false;
                }

                squareQueue.addAll(squareQueueToAdd);
                squareQueueToAdd = new ArrayList<Square>();
            }
            if (successMove != null)
                return successMove;
            else
                return Move.WAIT;
        }

        private int getTotalEstimatedCosts() {
            return getRealCosts() + getEstimatedCosts();
        }

        private int getRealCosts() {
            if (gotHereFrom == null) {
                return 0;
            }
            return 1 + gotHereFrom.getRealCosts();
        }

        private int getEstimatedCosts() {
            List<String> objectivePositions = findObjectives(objective);
            if (objectivePositions.size() > 0) {
                String squarePos = y + "_" + x;
                return estimateCostsBetween(squarePos, objectivePositions);
            }
            else
                return 10000000;

//            String objectivePosition = findObjective(objective);
//            if (objectivePosition != null) {
//                String squarePos = y + "_" + x;
//                return estimateCostsBetween(squarePos, objectivePosition);
//            }
//            else
//                return 10000000;
        }

        private int estimateCostsBetween(String squarePos, List<String> objectivePos) {
            ArrayList<Integer> distances = new ArrayList<Integer>(objectivePos.size());
            String[] squarePositions = squarePos.split("_");

            for (String op : objectivePos) {
                String[] objectivePositions = op.split("_");
                int yDistance = Math.abs(Integer.valueOf(squarePositions[0]) - Integer.valueOf(objectivePositions[0]));
                int xDistance = Math.abs(Integer.valueOf(squarePositions[1]) - Integer.valueOf(objectivePositions[1]));
                distances.add(yDistance + xDistance);
            }
            return Collections.min(distances);
        }

//        private int estimateCostsBetween(String squarePos, String objectivePos) {
//            String[] squarePositions = squarePos.split("_");
//            String[] objectivePositions = objectivePos.split("_");
//
//            int yDistance = Math.abs(Integer.valueOf(squarePositions[0]) - Integer.valueOf(objectivePositions[0]));
//            int xDistance = Math.abs(Integer.valueOf(squarePositions[1]) - Integer.valueOf(objectivePositions[1]));
//            return yDistance + xDistance;
//        }

        protected Move processSquareInQueue() {
            if (isSquareContainingObjective()) {
                return getFirstMoveFromRoot();
            } else {
                addNotVisitedAccessibleNeighbourSquaresToQueue(this, x, y);
                return null;
            }
        }

        private boolean isSquareBeforeRootSquare() {

            //no action since this is the root square
            if (gotHereFrom == null) {
                return false;
            }
            if (gotHereFrom.gotHereFrom == null) {
                return true;
            }
            return false;
        }

        private void addNotVisitedAccessibleNeighbourSquaresToQueue(Square origin, int xPos, int yPos) {
            //Add all valid neighbour Squares
            try {
                addSquareToQueueIfAccessible(new Square(map[yPos - 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() - 1, objective, Move.UP, this));
                addSquareToQueueIfAccessible(new Square(map[yPos + 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() + 1, objective, Move.DOWN, this));
                addSquareToQueueIfAccessible(new Square(map[yPos][xPos - 1], origin.getXCoordinate() - 1, origin.getYCoordinate(), objective, Move.LEFT, this));
                addSquareToQueueIfAccessible(new Square(map[yPos][xPos + 1], origin.getXCoordinate() + 1, origin.getYCoordinate(), objective, Move.RIGHT, this));
            } catch (ArrayIndexOutOfBoundsException e) {
                //do not add square since it is outside of the play board
            }
        }

        private void addSquareToQueueIfAccessible(Square square) {
            if (square.isSquareVisitable()) {
                squareQueueToAdd.add(square);
                visitedSquares.put(square.getStringCoordinate(), square);
            }
        }

        protected Move getFirstMoveFromRoot() {
            if (isSquareBeforeRootSquare()) {
                return howToGetHere;
            } else {
                if (gotHereFrom == null) {
                    return null;
                }
                return gotHereFrom.getFirstMoveFromRoot();
            }
        }

        private String getStringCoordinate() {
            return Integer.toString(x) + "_" + Integer.toString(y);
        }

        private boolean isSquareVisitable() {
            if (type == Type.FENCE) {
                return false;
            }

            if (visitedSquares.get(getStringCoordinate()) != null) {
                return false;
            }

            return true;
        }

        private boolean isSquareContainingObjective() {
            for (int i = 0; i < objective.length; i++) {
                if (type == objective[i]) {
                    return true;
                }
            }
            return false;
        }

        protected int getXCoordinate() {
            return x;
        }

        protected int getYCoordinate() {
            return y;
        }
    }
}